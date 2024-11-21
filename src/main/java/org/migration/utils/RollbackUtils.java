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

public class RollbackUtils {


    private static final MigrationExecutor migrationExecutor = MigrationExecutor.getInstance();
    private static final MigrationManager migrationManager = MigrationManager.getInstance();

    private static final Logger logger = LoggerFactory.getLogger(MigrationManager.class);


    public static void rollbackToPreviousVersion(List<File> fileList) {

        List<String> versions = migrationManager.getAllVersions();

        String rollbackVersion = versions.get(versions.size() - 2);
        logger.info("The version we should return to {}", rollbackVersion);

        File rollBackScript = fileList.stream()
                .filter(file -> file.getName().matches("U" + rollbackVersion + ".+.sql"))
                .findFirst().get();

        logger.info("Undo file for rollback {}", rollBackScript.getName());

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
                    return FileVersion.extractVersionFromFileName(file.getName()).compareTo(version) >= 0;
                })
                .sorted(Comparator.comparing(File::getName).reversed())
                .toList();

        logger.info("Files that should be executed {} to version {}", scriptsToExecute.stream()
                .map(File::getName)
                .collect(Collectors.joining(", ")), version);

        scriptsToExecute.forEach(migrationExecutor::executeSqlScript);

    }


}
