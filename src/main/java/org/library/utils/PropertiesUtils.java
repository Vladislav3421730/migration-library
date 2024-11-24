package org.library.utils;


import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

/**
 * Class for extracting data from application.properties
 */
@Slf4j
public class PropertiesUtils {
    
    private static final Properties properties=new Properties();
    
    static {
        log.info("Trying to getting resources from properties file");
        try {
            properties.load(PropertiesUtils.class.getClassLoader().getResourceAsStream("application.properties"));
            log.info("Properties loaded successfully");
        } catch (IOException e) {
            log.error("Failed to get resources  {}",e.getMessage());
            throw new RuntimeException("Ошибка загрузки конфигурации: " + "application.properties", e);
        }
    }

    /**
     * Getting the database url
     * @return url in String format
     */
    public static String getJdbcUrl() {
        return properties.getProperty("db.url");
    }

    /**
     * Getting username from db
     * @return username in String format
     */
    public static String getUsername() {
        return properties.getProperty("db.username");
    }

    /**
     * Getting password from db
     * @return password in String format
     */
    public static String getPassword() {
        return properties.getProperty("db.password");
    }

    /**
     * Getting the path to save .sql files in resources
     * @return path in resources in String format
     */
    public static String getDbMigrationPath(){
        return properties.getProperty("db.migration.path");
    }



}
