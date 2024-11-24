package org.library.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.library.dto.MigrationReport;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class JsonReport {

    /**
     * Saving reports in json format in the jsonReports folder
     * @param migrationReport object of class MigrationReport
     */
    public static void SaveReportInJson(MigrationReport migrationReport) {

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd__HH_mm_ss__SSS");
        String filePath = "jsonReports/report_" + now.format(formatter)+".json";

        log.info("Trying to generate format in file {}",filePath);

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            gson.toJson(migrationReport, fileWriter);
            log.info("report in file {} was successfully generated",filePath);
        } catch (IOException e) {
            log.error("Failed to create file {}", filePath,e);
        }
    }

}
