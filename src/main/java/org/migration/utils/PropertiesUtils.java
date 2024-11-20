package org.migration.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class PropertiesUtils {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);
    private static final Properties properties=new Properties();

    static {

        logger.info("Trying to getting resources from properties file");
        try {
            properties.load(PropertiesUtils.class.getClassLoader().getResourceAsStream("application.properties"));
            logger.info("Properties loaded successfully");
        } catch (IOException e) {
            logger.error("Failed to get resources  {}",e.getMessage());
            throw new RuntimeException("Ошибка загрузки конфигурации: " + "application.properties", e);
        }
    }

    public static String getJdbcUrl() {
        return properties.getProperty("db.url");
    }

    public static String getUsername() {
        return properties.getProperty("db.username");
    }


    public static String getPassword() {
        return properties.getProperty("db.password");
    }








}
