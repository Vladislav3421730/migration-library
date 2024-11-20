package org.migration.migration;


import org.migration.connection.ConnectionManager;
import org.migration.dto.MigrationReport;
import org.migration.utils.FileVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MigrationManager {

    private static final MigrationManager INSTANCE = new MigrationManager();

    public static MigrationManager getInstance() {
        return INSTANCE;
    }

    private MigrationManager() {
    }

    private final MigrationFileReader migrationFileReader= MigrationFileReader.getInstance();
    private final MigrationExecutor migrationExecutor = MigrationExecutor.getInstance();

    private static final Logger logger = LoggerFactory.getLogger(MigrationManager.class);

    private final String GET_ALL_MIGRATIONS = "SELECT version, script_name, executed_at, status FROM schema_history";
    private final String GET_CURRENT_MIGRATION = GET_ALL_MIGRATIONS + " ORDER BY executed_at DESC LIMIT 1";



    public List<MigrationReport> getAllMigrationReports() {

        ConnectionManager.connect();

        List<MigrationReport> migrationReports = new ArrayList<>();

        logger.info("Trying to get all reports...");
        try (var connection = ConnectionManager.getConnection()) {

            var statement = connection.createStatement();
            var resultSet = statement.executeQuery(GET_ALL_MIGRATIONS);

            logger.info("Getting {} reports", resultSet.getFetchSize());
            while (resultSet.next()) {

                MigrationReport migrationReport = new MigrationReport();
                migrationReport.setVersion(resultSet.getString("version"));
                migrationReport.setScript_name("script_name");
                migrationReport.setExecuted_at(resultSet.getTimestamp("executed_at"));
                migrationReport.setStatus(resultSet.getString("status"));
                migrationReports.add(migrationReport);

            }

            return migrationReports;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public MigrationReport getLastMigrationReport() {

        ConnectionManager.connect();

        MigrationReport migrationReport = new MigrationReport();

        logger.info("Trying to get last Report");
        try (var connection = ConnectionManager.getConnection()) {

            var statement = connection.createStatement();
            var resultSet = statement.executeQuery(GET_CURRENT_MIGRATION);
            if (resultSet.next()) {

                migrationReport.setVersion(resultSet.getString("version"));
                migrationReport.setScript_name("script_name");
                migrationReport.setExecuted_at(resultSet.getTimestamp("executed_at"));
                migrationReport.setStatus(resultSet.getString("status"));
            }

            return migrationReport;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public String getLastVersion() {
        return getLastMigrationReport().getVersion();
    }

    public List<String> getAllVersions() {

        return getAllMigrationReports().stream()
                .map(MigrationReport::getVersion)
                .collect(Collectors.toList());
    }

    public void rollback() {

        logger.info("Trying to get all undo files");
        List<File> files = migrationFileReader.readFilesFromResources('U');
        List<String> versions=getAllVersions();
        String rollbackVersion = versions.get(versions.size()-2);
        logger.info("The version we should return to {}",rollbackVersion);

        File rollBackScript = files.stream()
                .filter(file->file.getName().matches("U" + rollbackVersion+ ".+.sql"))
                .findFirst().get();

        logger.info("Undo file for rollback {}",rollBackScript.getName());

        migrationExecutor.executeSqlScript(rollBackScript);

        logger.info("Version rolled back successfully to version {}",rollbackVersion);

    }

    public void rollback(String version){

        logger.info("Trying to get all undo files");
        List<String> allVersions=getAllVersions();
        Optional<String> isCurrentVersion = allVersions.stream().filter(x -> x.equals(version)).findFirst();

        if(!isCurrentVersion.isPresent()) {
            logger.error("Version {} wasn't found in all versions {}",version,allVersions);
            return;
        }

        List<File> scriptsToExecute = migrationFileReader.readFilesFromResources('U').stream()
                .filter(file -> {
                    return  FileVersion.extractVersionFromFileName(file.getName()).compareTo(version)>0 ||
                            FileVersion.extractVersionFromFileName(file.getName()).equals(version);
                })
                .sorted(Comparator.comparing(File::getName).reversed())
                .toList();

        logger.info("Files that should be executed {} to version {}", scriptsToExecute.stream()
                .map(File::getName)
                .collect(Collectors.joining(", ")),version);

        scriptsToExecute.forEach(migrationExecutor::executeSqlScript);
    }

}
