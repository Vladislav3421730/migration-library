package org.migration.migration;


import org.migration.connection.ConnectionManager;
import org.migration.dto.MigrationReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MigrationManager {

    private static final MigrationManager INSTANCE = new MigrationManager();

    public static MigrationManager getInstance() {
        return INSTANCE;
    }
    private MigrationManager() {}

    private static final Logger logger = LoggerFactory.getLogger(MigrationManager.class);

    private final String GET_ALL_MIGRATIONS="SELECT (version, script_name, executed_at, status) FROM schema_history";
    private final String GET_CURRENT_MIGRATION=GET_ALL_MIGRATIONS + " ORDER BY executed_at DESC LIMIT 1";

    public List<MigrationReport> getAllMigrationReports(){

        ConnectionManager.connect();

        List<MigrationReport> migrationReports=new ArrayList<>();

        logger.info("Trying to get all reports...");
        try(var connection=ConnectionManager.getConnection()) {

            var statement=connection.createStatement();
            var resultSet = statement.executeQuery(GET_ALL_MIGRATIONS);

            logger.info("Getting {} reports",resultSet.getFetchSize());
            while (resultSet.next()){

                MigrationReport migrationReport=new MigrationReport();
                migrationReport.setId(resultSet.getInt("id"));
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

    public MigrationReport getLastMigrationReport(){

        ConnectionManager.connect();

        MigrationReport migrationReport=new MigrationReport();

        logger.info("Trying to get last Report");
        try(var connection=ConnectionManager.getConnection()) {

            var statement=connection.createStatement();
            var resultSet = statement.executeQuery(GET_CURRENT_MIGRATION);
            if(resultSet.next()){
                migrationReport.setId(resultSet.getInt("id"));
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


}
