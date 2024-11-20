package org.migration.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MigrationFileReader {

    private static final MigrationFileReader INSTANCE = new MigrationFileReader();

    public static MigrationFileReader getInstance() {
        return INSTANCE;
    }

    private MigrationFileReader() {
    }

    private static final Logger logger = LoggerFactory.getLogger(MigrationFileReader.class);

    private final static String SQL_FILES_PATH = "db/migration";

    public List<File> readFilesFromResources(char migrationIndicator) {

        List<File> migrationFiles = new ArrayList<>();
        try {

            logger.info("Searching for migration files in resources: {}", SQL_FILES_PATH);

            File folder = new File(getClass().getClassLoader().getResource(SQL_FILES_PATH).getFile());
            addFilesToFileList(folder, migrationFiles,migrationIndicator);
        } catch (NullPointerException e) {
            logger.error("Path {} didn't exist {}", SQL_FILES_PATH, e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error while reading files from resources: {}", e.getMessage(), e);
        }
        return migrationFiles;
    }


    public List<File> readFilesFromExternalDirectory(String directoryPath,char migrationIndicator) {
        List<File> migrationFiles = new ArrayList<>();
        try {
            logger.info("Searching for migration files in external directory: {}", directoryPath);
            File folder = new File(directoryPath);
            addFilesToFileList(folder, migrationFiles,migrationIndicator);
        } catch (NullPointerException e) {
            logger.error("Path {} didn't exist {}", SQL_FILES_PATH, e.getMessage());
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.error("Error while reading files from external directory: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return migrationFiles;
    }

    public String getScriptFromSqlFile(File file) {

        String lines;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            logger.info("Trying to get script from file {}", file.getName());
            lines = reader.lines().collect(Collectors.joining());
            logger.info("Successfully getting script from file {} : {}", file.getName(), lines);

        } catch (FileNotFoundException e) {

            logger.error("File wasn't found: {}", e.getMessage(), e);

            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return lines;
    }

    private void addFilesToFileList(File folder, List<File> files,char migrationIndicator) {

        if (folder.isDirectory()) {
            char g='l';
            for (File file : folder.listFiles()) {
                if (file.getName().matches("^"+migrationIndicator+".+__.*\\.sql$")) {
                    files.add(file);
                    logger.info("Found migration file: {}", file.getName());
                } else {
                    logger.error("Error with file name {}", file.getName());
                }
            }
        } else {
            logger.error("Resource path is not a directory: {}", SQL_FILES_PATH);
        }

    }

}
