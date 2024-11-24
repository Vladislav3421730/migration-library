package org.library.migration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.library.utils.PropertiesUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The class is designed to work with files:
 * search for files with the required prefix, extract a script from a file
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MigrationFileReader {

    private static final MigrationFileReader INSTANCE = new MigrationFileReader();
    public static MigrationFileReader getInstance() {
        return INSTANCE;
    }

    private final static String SQL_FILES_PATH = PropertiesUtils.getDbMigrationPath();

    /**
     * Method fo finding all .sql files in resources in directory db/migration
     * @param migrationIndicator Shows which files to search for (with U or V prefix)
     * @return List of found files in resources
     */
    public List<File> readFilesFromResources(char migrationIndicator) {

        List<File> migrationFiles = new ArrayList<>();

        try {
            log.info("Searching for migration files in resources: {}", SQL_FILES_PATH);
            File folder = new File(getClass().getClassLoader().getResource(SQL_FILES_PATH).getFile());
            addFilesToFileList(folder, migrationFiles,migrationIndicator);
        } catch (NullPointerException e) {
            log.error("Path {} didn't exist {}", SQL_FILES_PATH, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error while reading files from resources: {}", e.getMessage(), e);
        }
        return migrationFiles;
    }

    /**
     * method fo finding all .sql files in some directory in project
     * @param directoryPath  Path to the folder with files
     * @param migrationIndicator Shows which files to search for (with U or V prefix)
     * @return List of found files in introduced directory
     */
    public List<File> readFilesFromExternalDirectory(String directoryPath,char migrationIndicator) {
        List<File> migrationFiles = new ArrayList<>();
        try {
            log.info("Searching for migration files in external directory: {}", directoryPath);
            File folder = new File(directoryPath);
            addFilesToFileList(folder, migrationFiles,migrationIndicator);
        } catch (NullPointerException e) {
            log.error("Path {} didn't exist {}", SQL_FILES_PATH, e.getMessage());
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("Error while reading files from external directory: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return migrationFiles;
    }

    /**
     * Reads sql script from file
     * @param file File containing sql script
     * @return sql script as a string
     */
    public String getScriptFromSqlFile(File file) {

        String lines;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            log.info("Trying to get script from file {}", file.getName());
            lines = reader.lines().collect(Collectors.joining());
            log.info("Successfully getting script from file {} : {}", file.getName(), lines);

        } catch (FileNotFoundException e) {

            log.error("File wasn't found: {}", e.getMessage(), e);

            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return lines;
    }

    private void addFilesToFileList(File folder, List<File> files,char migrationIndicator) {

        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (file.getName().matches("^"+migrationIndicator+".+__.*\\.sql$")) {
                    files.add(file);
                    log.info("Found migration file: {}", file.getName());
                } else {
                    log.error("Error with file name {}", file.getName());
                }
            }
        } else {
            log.error("Resource path is not a directory: {}", SQL_FILES_PATH);
        }

    }

}
