package org.library.migration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.library.connection.ConnectionManager;
import org.library.dto.MigrationReport;
import org.library.utils.FileVersion;
import org.library.utils.HistoryTableGeneration;
import org.library.utils.JsonReport;


import java.io.File;
import java.sql.*;
import java.util.List;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MigrationExecutor {

    private static final MigrationExecutor INSTANCE = new MigrationExecutor();
    public static MigrationExecutor getInstance() {
        return INSTANCE;
    }

    private final MigrationFileReader migrationFileReader = MigrationFileReader.getInstance();
    
    private final String GET_LOCK = "SELECT is_locked FROM migration_lock";
    private final String SET_MIGRATION_LOCK = "UPDATE migration_lock SET is_locked = ?";
    private final String ADD_NEW_MIGRATION = "INSERT INTO schema_history (version, script_name, executed_at, status) VALUES (?, ?, ?, ?)";

    /**
     * Method for executing a list of files with sql scripts
     * @param fileList List of all files (no matter with U or V prefix)
     */
    public void executeSqlScript(List<File> fileList) {

        String filename = null;
        ConnectionManager.connect();
        try (Connection connection = ConnectionManager.getConnection()) {
            HistoryTableGeneration.generateScript(connection);
            if (isLocked(connection)) {
                log.error("Migration is locked");
                throw new RuntimeException("Migration lock is active. Another process may be running migrations.");
            }
            executeLisFileSQL(connection,fileList,filename);
        } catch (SQLException e) {
            log.error("Database error: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void executeLisFileSQL(Connection connection,List<File> scriptFile,String filename) throws SQLException {

        setLock(connection, true);
        connection.setAutoCommit(false);

        try {

            for (File sqlScriptFile : scriptFile) {

                filename=sqlScriptFile.getName();
                String sqlScript = migrationFileReader.getScriptFromSqlFile(sqlScriptFile);
                log.info("Trying to execute query: {}", sqlScript);
                try (var statement = connection.prepareStatement(sqlScript)) {
                    statement.execute();
                    MigrationReport successfulMigrationReport = saveMigrationStatus(connection, sqlScriptFile.getName(), "SUCCESS");
                    JsonReport.SaveReportInJson(successfulMigrationReport);
                    log.info("Script executed successfully: {}", sqlScriptFile.getName());
                }
            }

            connection.commit();
            log.info("All scripts executed successfully. Transaction committed.");

        } catch (SQLException e) {
            connection.rollback();
            MigrationReport failedMigrationReport = saveMigrationStatus(connection, filename, "FAILED");
            JsonReport.SaveReportInJson(failedMigrationReport);
            log.error("Transaction rolled back due to an error: {}", e.getMessage(),e);
        } finally {
            setLock(connection,false);
            connection.commit();
        }
    }

    private boolean isLocked(Connection connection) throws SQLException {

        try (var statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_LOCK)) {
            if (resultSet.next()) {
                return resultSet.getBoolean("is_locked");
            }
            log.error("Lock table is empty. Initialization issue?");
            return true;
        }
    }

    private void setLock(Connection connection, boolean lock) throws SQLException {

        try (PreparedStatement statement = connection.prepareStatement(SET_MIGRATION_LOCK)) {
            statement.setBoolean(1, lock);
            statement.executeUpdate();
            log.info("Migration lock set to: {}", lock);
        }
    }


    private MigrationReport saveMigrationStatus(Connection connection, String scriptName, String status) {

        try (PreparedStatement statement = connection.prepareStatement(ADD_NEW_MIGRATION)) {

            MigrationReport migrationReport = MigrationReport.builder()
                    .version(FileVersion.extractVersionFromFileName(scriptName))
                    .script_name(scriptName)
                    .executed_at(new Timestamp(System.currentTimeMillis()))
                    .status(status)
                    .build();

            statement.setString(1, migrationReport.getVersion());
            statement.setString(2, migrationReport.getScript_name());
            statement.setTimestamp(3, migrationReport.getExecuted_at());
            statement.setString(4, migrationReport.getStatus());
            statement.executeUpdate();

            log.info("Migration status saved: script={}, status={}", scriptName, status);

            return migrationReport;

        } catch (SQLException e) {
            log.error("Error saving migration status for script {}: {}", scriptName, e.getMessage());
            throw new RuntimeException("Failed to save migration status for " + scriptName, e);
        }
    }
}
