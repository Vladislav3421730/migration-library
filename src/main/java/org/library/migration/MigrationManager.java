package org.library.migration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.library.connection.ConnectionManager;
import org.library.dto.MigrationReport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MigrationManager {

    private static final MigrationManager INSTANCE = new MigrationManager();
    public static MigrationManager getInstance() {
        return INSTANCE;
    }

    private final String GET_ALL_MIGRATIONS = "SELECT version, script_name, executed_at, status FROM schema_history";
    private final String GET_CURRENT_MIGRATION = GET_ALL_MIGRATIONS + " ORDER BY executed_at DESC LIMIT 1";
    private final String GET_CURRENT_VERSION=GET_ALL_MIGRATIONS+ " WHERE status='SUCCESS'" + " ORDER BY executed_at DESC LIMIT 1";

    /**
     * Getting a list of all migrations (all rows from the migration_history table)
     * @return List of all reports
     */
    public List<MigrationReport> getAllMigrationReports() {

        ConnectionManager.connect();
        List<MigrationReport> migrationReports = new ArrayList<>();
        log.info("Trying to get all reports...");
        try (var connection = ConnectionManager.getConnection()) {
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery(GET_ALL_MIGRATIONS);
            while (resultSet.next()) {
                migrationReports.add(getMigration(resultSet));
            }
            log.info("Successfully received {} reports", migrationReports);
            return migrationReports;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Getting the latest report (last row in the migration_history table)
     * @return The last report
     */
    public MigrationReport getLastMigrationReport() {

        ConnectionManager.connect();
        MigrationReport migrationReport = new MigrationReport();
        log.info("Trying to get last Report");
        try (var connection = ConnectionManager.getConnection()) {
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery(GET_CURRENT_MIGRATION);
            if (resultSet.next()) {
             migrationReport=getMigration(resultSet);
            }
            log.info("Successfully received report, version {}",migrationReport.getVersion());
            return migrationReport;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Getting the current latest version of the database with SUCCESS status
     * @return version in String format
     */
    public String getLastVersion() {
        ConnectionManager.connect();
        MigrationReport migrationReport = new MigrationReport();
        log.info("Trying to get last Report");
        try (var connection = ConnectionManager.getConnection()) {
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery(GET_CURRENT_VERSION);
            if (resultSet.next()) {
                migrationReport=getMigration(resultSet);
            }
            return migrationReport.getVersion();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Getting all db versions with any status
     * @return List of versions in String format
     */
    public List<String> getAllVersions() {

        return getAllMigrationReports().stream()
                .map(MigrationReport::getVersion)
                .collect(Collectors.toList());
    }
    
    private MigrationReport getMigration(ResultSet resultSet) throws SQLException {
        
       return MigrationReport.builder()
               .version(resultSet.getString("version"))
               .executed_at(resultSet.getTimestamp("executed_at"))
               .script_name(resultSet.getString("script_name"))
               .status(resultSet.getString("status"))
               .build();
    }
}
