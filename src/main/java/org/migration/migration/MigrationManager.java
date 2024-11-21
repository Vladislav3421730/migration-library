package org.migration.migration;


import org.migration.connection.ConnectionManager;
import org.migration.dto.MigrationReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.migration.utils.RollbackUtils.rollbackToPreviousVersion;
import static org.migration.utils.RollbackUtils.rollbackToSomeVersion;

public class MigrationManager {

    private static final MigrationManager INSTANCE = new MigrationManager();

    public static MigrationManager getInstance() {
        return INSTANCE;
    }

    private MigrationManager() {
    }

    private final MigrationFileReader migrationFileReader= MigrationFileReader.getInstance();

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


            while (resultSet.next()) {

                MigrationReport migrationReport = new MigrationReport();
                migrationReport.setVersion(resultSet.getString("version"));
                migrationReport.setScript_name("script_name");
                migrationReport.setExecuted_at(resultSet.getTimestamp("executed_at"));
                migrationReport.setStatus(resultSet.getString("status"));
                migrationReports.add(migrationReport);

            }
            logger.info("Getting {} reports", migrationReports.size());
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

        logger.info("Trying to get all undo files from resources");
        rollbackToPreviousVersion(migrationFileReader.readFilesFromResources('U'));

    }

    public void rollbackToVersionByResources(String version){

        logger.info("Trying to get all undo files from resources and roll back to version {}",version);
        rollbackToSomeVersion(migrationFileReader.readFilesFromResources('U'),version);

    }

    public void rollback(String path){
        logger.info("Trying to get all undo files from external directory {}",path);
        rollbackToPreviousVersion(migrationFileReader.readFilesFromExternalDirectory(path,'U'));

    }

    public void rollback(String path,String version){

        logger.info("Trying to get all undo files from resources and roll back to version {}",version);
        rollbackToSomeVersion(migrationFileReader.readFilesFromExternalDirectory(path,'U'),version);
    }




}
