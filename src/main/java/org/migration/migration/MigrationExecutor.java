package org.migration.migration;

import org.migration.connection.ConnectionManager;
import org.migration.dto.MigrationReport;
import org.migration.utils.FileVersion;
import org.migration.utils.HistoryTableGeneration;
import org.migration.utils.JsonReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.*;

public class MigrationExecutor {

    private static final MigrationExecutor INSTANCE = new MigrationExecutor();

    public static MigrationExecutor getInstance() {
        return INSTANCE;
    }

    private MigrationExecutor() {
    }

    private final MigrationFileReader migrationFileReader = MigrationFileReader.getInstance();

    private static final Logger logger = LoggerFactory.getLogger(MigrationExecutor.class);
    private final String GET_LOCK = "SELECT is_locked FROM migration_lock";
    private final String SET_MIGRATION_LOCK = "UPDATE migration_lock SET is_locked = ?";
    private final String ADD_NEW_MIGRATION = "INSERT INTO schema_history (version, script_name, executed_at, status) VALUES (?, ?, ?, ?)";

    public void executeSqlScript(File scriptFile) {

        ConnectionManager.connect();

        String sqlScript = migrationFileReader.getScriptFromSqlFile(scriptFile);

        try (Connection connection = ConnectionManager.getConnection()) {

            HistoryTableGeneration.generateScript(connection);

            if (isLocked(connection)) {
                logger.error("Migration is locked {}", scriptFile.getName());
                throw new RuntimeException("Migration lock is active. Another process may be running migrations.");
            }
            setLock(connection, true);

            try {
                logger.info("Trying to execute query: {}", sqlScript);

                var statement = connection.prepareStatement(sqlScript);

                statement.execute();
                MigrationReport successfulMigrationReport = saveMigrationStatus(connection, scriptFile.getName(), "SUCCESS");
                JsonReport.SaveReportInJson(successfulMigrationReport);


                logger.info("Script executed successfully: {}", scriptFile.getName());
            } catch (Exception e) {

                logger.error("Error executing script {}: {}", scriptFile.getName(), e.getMessage());
                MigrationReport failedMigration = saveMigrationStatus(connection, scriptFile.getName(), "FAILED");
                JsonReport.SaveReportInJson(failedMigration);
                throw new RuntimeException("Failed to execute script " + scriptFile.getName(), e);
            } finally {
                setLock(connection, false);
            }
        } catch (Exception e) {
            logger.error("Database error: {}", e.getMessage());
            throw new RuntimeException("Database error occurred", e);
        }
    }

    private boolean isLocked(Connection connection) throws SQLException {

        try (var statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_LOCK)) {
            if (resultSet.next()) {
                return resultSet.getBoolean("is_locked");
            }
            throw new RuntimeException("Lock table is empty. Initialization issue?");
        }
    }

    private void setLock(Connection connection, boolean lock) throws SQLException {

        try (PreparedStatement statement = connection.prepareStatement(SET_MIGRATION_LOCK)) {
            statement.setBoolean(1, lock);
            statement.executeUpdate();
            logger.info("Migration lock set to: {}", lock);
        }
    }


    private MigrationReport saveMigrationStatus(Connection connection, String scriptName, String status) {

        try (PreparedStatement statement = connection.prepareStatement(ADD_NEW_MIGRATION)) {

            MigrationReport migrationReport = new MigrationReport(FileVersion.extractVersionFromFileName(scriptName),
                    scriptName,
                    new Timestamp(System.currentTimeMillis()),
                    status);

            statement.setString(1, migrationReport.getVersion());
            statement.setString(2, migrationReport.getScript_name());
            statement.setTimestamp(3, migrationReport.getExecuted_at());
            statement.setString(4, migrationReport.getStatus());
            statement.executeUpdate();

            logger.info("Migration status saved: script={}, status={}", scriptName, status);

            return migrationReport;

        } catch (SQLException e) {
            logger.error("Error saving migration status for script {}: {}", scriptName, e.getMessage());
            throw new RuntimeException("Failed to save migration status for " + scriptName, e);
        }
    }
}
