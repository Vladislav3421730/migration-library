package org.migration.connection;

import org.migration.utils.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

    private static Connection connection=null;

    public static void connect()  {
        try {
            logger.info("Trying to connect to db url {} username {} password {}",PropertiesUtils.getJdbcUrl(),PropertiesUtils.getUsername(),PropertiesUtils.getPassword());

            connection = DriverManager.getConnection(
                    PropertiesUtils.getJdbcUrl(),
                    PropertiesUtils.getUsername(),
                    PropertiesUtils.getPassword());

            logger.info("Successfully connected to database");
        } catch (SQLException e) {
            logger.error("Failed to connect to db url {} username {} password {}",PropertiesUtils.getJdbcUrl(),PropertiesUtils.getUsername(),PropertiesUtils.getPassword());
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection(){
        return connection;
    }


}
