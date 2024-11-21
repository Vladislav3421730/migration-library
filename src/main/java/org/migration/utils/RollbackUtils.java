package org.migration.utils;

import org.migration.migration.MigrationExecutor;
import org.migration.migration.MigrationManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.migration.utils.FileVersion.extractVersionFromFileName;

public class RollbackUtils {


    private static final MigrationExecutor migrationExecutor = MigrationExecutor.getInstance();
    private static final MigrationManager migrationManager = MigrationManager.getInstance();

    private static final Logger logger = LoggerFactory.getLogger(MigrationManager.class);


    public static void rollbackToPreviousVersion(List<File> fileList) {

        List<String> versions = migrationManager.getAllVersions();
        String rollbackVersion;

        Optional<String> rollbackVersionOpt = versions.stream()
                .filter(x -> x.compareTo(migrationManager.getLastVersion()) < 0)
                .max(Comparator.comparing(String::valueOf));

        if (rollbackVersionOpt.isPresent()) {
            rollbackVersion = rollbackVersionOpt.get();
            logger.info("The version we should return to {}", rollbackVersion);
        } else {
            logger.error("No rollback version found for the current version: {}", migrationManager.getLastVersion());
            return;
        }

        List<File> rollBackScript = fileList.stream()
                .filter(file -> file.getName().matches("U" + rollbackVersion + ".+.sql"))
                .collect(Collectors.toList());

        logger.info("Undo file for rollback {}", rollBackScript.get(0).getName());

        migrationExecutor.executeSqlScript(rollBackScript);

        logger.info("Version rolled back successfully to version {}", rollbackVersion);

    }

    public static void rollbackToSomeVersion(List<File> fileList, String version) {

        logger.info("Trying to get all undo files");
        List<String> allVersions = migrationManager.getAllVersions();

        Optional<String> isCurrentVersion = allVersions.stream().filter(x -> x.equals(version)).findFirst();

        if (!isCurrentVersion.isPresent()) {
            logger.error("Version {} wasn't found in all versions {}", version, allVersions);
            return;
        }

        List<File> scriptsToExecute = fileList.stream()
                .filter(file -> {
                    return extractVersionFromFileName(file.getName()).compareTo(version) >= 0 &&
                            extractVersionFromFileName(file.getName())
                                    .compareTo(migrationManager.getLastVersion()) < 0;
                })
                .sorted(Comparator.comparing(File::getName).reversed())
                .collect(Collectors.toList());

        logger.info("Files that should be executed {} to version {}", scriptsToExecute.stream()
                .map(File::getName)
                .collect(Collectors.joining(", ")), version);

        migrationExecutor.executeSqlScript(scriptsToExecute);

    }


}
