package org.library.utils;

import lombok.extern.slf4j.Slf4j;
import org.library.migration.MigrationExecutor;
import org.library.migration.MigrationManager;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.library.utils.FileVersion.extractVersionFromFileName;

@Slf4j
public class RollbackUtils {

    private static final MigrationExecutor migrationExecutor = MigrationExecutor.getInstance();
    private static final MigrationManager migrationManager = MigrationManager.getInstance();

    /**
     * Rollback to previous version
     * @param fileList Undo file list
     */
    public static void rollbackToPreviousVersion(List<File> fileList) {

        String rollbackVersion = findPreviousVersion();
        log.info("The version we should return to {}", rollbackVersion);
        //finding first file, which version is lower then current version
        List<File> rollBackScript = fileList.stream()
                .filter(file -> file.getName().matches("U" + rollbackVersion + ".+.sql"))
                .collect(Collectors.toList());

        log.info("Undo file for rollback {}", rollBackScript.get(0).getName());
        //execute the script of the found file
        migrationExecutor.executeSqlScript(rollBackScript);
        log.info("Version rolled back successfully to version {}", rollbackVersion);

    }

    /**
     * Rollback to the specified version
     * @param fileList fileList Undo file list
     * @param version
     */
    public static void rollbackToSomeVersion(List<File> fileList, String version) {

        log.info("Trying to get all undo files");
        List<String> allVersions = migrationManager.getAllVersions();
        //checking if the version we want to revert exists
        if (!allVersions.contains(version)) {
            log.error("Version {} wasn't found in all versions {}", version, allVersions);
            return;
        }
        List<File> scriptsToExecute = findUndoFilesBetweenCurrentAndIntroducedVersion(fileList, version);
        log.info("Files that should be executed {} to version {}", scriptsToExecute.stream()
                .map(File::getName)
                .collect(Collectors.joining(", ")), version);
        //execute all undo files
        migrationExecutor.executeSqlScript(scriptsToExecute);
    }

    private static String findPreviousVersion() {

        List<String> versions = migrationManager.getAllVersions();
        // check if previous version exists
        Optional<String> rollbackVersionOpt = versions.stream()
                .filter(x -> x.compareTo(migrationManager.getLastVersion()) < 0)
                .max(Comparator.comparing(String::valueOf));

        if (rollbackVersionOpt.isPresent()) {
            return rollbackVersionOpt.get();
        } else {
            log.error("No rollback version found for the current version: {}", migrationManager.getLastVersion());
            throw new RuntimeException();
        }
    }

    private static List<File> findUndoFilesBetweenCurrentAndIntroducedVersion(List<File> fileList, String version) {
        return fileList.stream()
                //find all files, which version is more or equal than version, which was introduced
                //and lower than current version
                .filter(file ->
                        extractVersionFromFileName(file.getName()).compareTo(version) >= 0 &&
                                extractVersionFromFileName(file.getName())
                                        .compareTo(migrationManager.getLastVersion()) < 0
                )
                //sort undo files in reversed queue
                .sorted((fileFirst, fileSecond) ->
                        extractVersionFromFileName(fileSecond.getName())
                                .compareTo(extractVersionFromFileName(fileFirst.getName())))
                .collect(Collectors.toList());
    }

}
