package org.migration.migration;

import org.migration.connection.ConnectionManager;
import org.migration.utils.FileVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.*;

public class MigrationExecutor {

    private static final MigrationExecutor INSTANCE = new MigrationExecutor();

    public static MigrationExecutor getInstance() {
        return INSTANCE;
    }
    private MigrationExecutor() {}

    private final MigrationFileReader migrationFileReader= MigrationFileReader.getInstance();

    private static final Logger logger = LoggerFactory.getLogger(MigrationExecutor.class);
    private final String GET_LOCK = "SELECT is_locked FROM migration_lock";
    private final String SET_MIGRATION_LOCK = "UPDATE migration_lock SET lock = ?";
    private final String ADD_NEW_MIGRATION = "INSERT INTO schema_history (version, script_name, executed_at, status) VALUES (?, ?, ?, ?)";

    public void executeSqlScript(File scriptFile) {

        ConnectionManager.connect();

        String sqlScript= migrationFileReader.getScriptFromSqlFile(scriptFile);

        try (Connection connection = ConnectionManager.getConnection()) {

            if (isLocked(connection)) {
                logger.error("Migration is locked {}", scriptFile.getName());
                throw new RuntimeException("Migration lock is active. Another process may be running migrations.");
            }
            setLock(connection, true);

            try {

                connection.setAutoCommit(false);
                logger.info("Trying to execute query: {}", sqlScript);

                var statement=connection.prepareStatement(sqlScript);

                statement.executeQuery();
                saveMigrationStatus(connection,scriptFile.getName(), "SUCCESS");

                connection.commit();

                logger.info("Script executed successfully: {}", scriptFile.getName());
            } catch (SQLException e) {

                connection.rollback();
                logger.error("Error executing script {}: {}", scriptFile.getName(), e.getMessage());
                saveMigrationStatus(connection,scriptFile.getName(), "FAILED");
                throw new RuntimeException("Failed to execute script " + scriptFile.getName(), e);
            } finally {
                setLock(connection, false);
            }
        } catch (SQLException e) {
            logger.error("Database error: {}", e.getMessage());
            throw new RuntimeException("Database error occurred", e);
        }
    }

    private boolean isLocked(Connection connection) throws SQLException {

        try (var statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_LOCK)) {
            if (resultSet.next()) {
                return resultSet.getBoolean("lock");
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


    private void saveMigrationStatus(Connection connection,String scriptName, String status) {

        try (PreparedStatement statement = connection.prepareStatement(ADD_NEW_MIGRATION)) {

            statement.setString(1, FileVersion.extractVersionFromFileName(scriptName));
            statement.setString(2, scriptName);
            statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            statement.setString(4, status);
            statement.executeUpdate();

            logger.info("Migration status saved: script={}, status={}", scriptName, status);
        } catch (SQLException e) {
            logger.error("Error saving migration status for script {}: {}", scriptName, e.getMessage());
            throw new RuntimeException("Failed to save migration status for " + scriptName, e);
        }
    }
}
