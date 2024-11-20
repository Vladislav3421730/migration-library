package org.migration.migration;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.migration.utils.FileVersion.extractVersionFromFileName;

public class MigrationTool {

    private static final Logger logger = LoggerFactory.getLogger(MigrationTool.class);
    private final MigrationFileReader migrationFileReader=MigrationFileReader.getInstance();
    private final MigrationExecutor migrationExecutor=MigrationExecutor.getInstance();


    public void executeAllMigrations() {
       execute(migrationFileReader.readFilesFromResources());
    }

    public void executeAllMigrations(String filePath){
        execute(migrationFileReader.readFilesFromExternalDirectory(filePath));
    }

    private void execute(List<File> sqlScripts){

        logger.info("Trying sorting sqlScripts files {}", sqlScripts.stream()
                .map(File::getName)
                .collect(Collectors.joining(", ")));

        sqlScripts.sort(Comparator.comparingInt(firstFile -> extractVersionFromFileName(firstFile.getName())));

        logger.info("sqlScripts files after sorting {}", sqlScripts.stream()
                .map(File::getName)
                .collect(Collectors.joining(", ")));

        sqlScripts.forEach((sqlScript) ->{
            saveMigrationRecord();
        });
    }

}
