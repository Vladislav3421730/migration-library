package org.migration.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.migration.connection.ConnectionManager;
import org.migration.dto.MigrationReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JsonReport {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

    public static void SaveReportInJson(MigrationReport migrationReport) {

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        LocalDateTime now = LocalDateTime.now();


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd__HH_mm_ss__SSS");
        String filePath = "jsonReports/report_" + now.format(formatter)+".json";

        logger.info("Trying to generate format in file {}",filePath);

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            gson.toJson(migrationReport, fileWriter);
            logger.info("report in file {} was successfully generated",filePath);
        } catch (IOException e) {
            logger.error("Failed to create file {}", filePath,e);
            throw new RuntimeException(e);
        }
    }

}
