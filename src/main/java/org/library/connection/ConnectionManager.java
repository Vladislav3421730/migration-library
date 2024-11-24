package org.library.connection;

import lombok.extern.slf4j.Slf4j;
import org.library.utils.PropertiesUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class for connecting to postgres database
 * This class provides methods to establish and retrieve a connection to the database
 */
@Slf4j
public class ConnectionManager {

    private static Connection connection = null;

    /**
     * Establishes a connection to the PostgreSQL database.
     * @throws RuntimeException if a {@link SQLException} occurs during the connection attempt.
     */
    public static void connect() {
        try {
            log.info("Trying to connect to db url {} username {} password {}", PropertiesUtils.getJdbcUrl(), PropertiesUtils.getUsername(), PropertiesUtils.getPassword());

            connection = DriverManager.getConnection(
                    PropertiesUtils.getJdbcUrl(),
                    PropertiesUtils.getUsername(),
                    PropertiesUtils.getPassword());

            log.info("Successfully connected to database");
        } catch (SQLException e) {
            log.error("Failed to connect to db url {} username {} password {}", PropertiesUtils.getJdbcUrl(), PropertiesUtils.getUsername(), PropertiesUtils.getPassword());
            throw new RuntimeException(e);
        }
    }

    /**
     * Method for getting connection with db
     * @return connection with postgresql
     */
    public static Connection getConnection() {
        return connection;
    }


}
