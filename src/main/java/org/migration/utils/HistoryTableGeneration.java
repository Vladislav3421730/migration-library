package org.migration.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class HistoryTableGeneration {

    private static final Logger logger = LoggerFactory.getLogger(HistoryTableGeneration.class);

    private static final String INIT_SQL = "CREATE TABLE IF NOT EXISTS schema_history\n" +
            "(\n" +
            "    id                SERIAL PRIMARY KEY,\n" +
            "    version           VARCHAR(50)  NOT NULL,\n" +
            "    script_name       VARCHAR(255) NOT NULL,\n" +
            "    executed_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
            "    status            VARCHAR(255)\n"+
            ");\n" +
            "\n" +
            "CREATE TABLE IF NOT EXISTS migration_lock\n" +
            "(\n" +
            "   is_locked BOOLEAN NOT NULL DEFAULT FALSE\n" +
            ");";

    private static final String CHECK_LOCK_TABLE = "SELECT * FROM migration_lock;";
    private static final String INSERT_LOCK = "INSERT INTO migration_lock VALUES (FALSE);";

    public static void generateScript(Connection connection) {
        try(var statement = connection.createStatement()) {

            statement.execute(INIT_SQL);

            var resultSet = statement.executeQuery(CHECK_LOCK_TABLE);
            if(!resultSet.next()) {

                statement.execute(INSERT_LOCK);
                logger.info("Generate tables schema_history and  migration_lock");
            }
            else {
                logger.info("Tables schema_history and migration_lock were already in db");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
