package org.library.utils;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * the class is used to generate the migration_history tables (similar to flyway_scema_history)
 * and migration_lock (to prevent simultaneous migrations)
 */
@Slf4j
public class HistoryTableGeneration {

    private static final String INIT_SQL = """
            CREATE TABLE IF NOT EXISTS schema_history(
                     id                SERIAL PRIMARY KEY,
                     version           VARCHAR(50)  NOT NULL,
                     script_name       VARCHAR(255) NOT NULL,
                     executed_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                     status            VARCHAR(255)
            );
            CREATE TABLE IF NOT EXISTS migration_lock(
            is_locked BOOLEAN NOT NULL
            );
            """;

    private static final String CHECK_LOCK_TABLE = "SELECT * FROM migration_lock;";
    private static final String INSERT_LOCK = "INSERT INTO migration_lock VALUES (FALSE);";

    /**
     * Method for generating migration_history and migration_lock tables
     * @param connection connection to db
     */
    public static void generateScript(Connection connection) {

        try (var statement = connection.createStatement()) {
            statement.execute(INIT_SQL);
            var resultSet = statement.executeQuery(CHECK_LOCK_TABLE);
            if (!resultSet.next()) {
                statement.execute(INSERT_LOCK);
                log.info("Generate tables schema_history and  migration_lock");
            } else {
                log.info("Tables schema_history and migration_lock were already in db");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
