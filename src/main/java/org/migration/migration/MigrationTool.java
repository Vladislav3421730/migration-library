package org.migration.migration;

import org.flywaydb.core.Flyway;

import org.flywaydb.core.api.FlywayException;
import org.migration.utils.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static org.migration.utils.FileVersion.extractVersionFromFileName;

public class MigrationTool {

    private static final Logger logger = LoggerFactory.getLogger(MigrationTool.class);
    private final MigrationFileReader migrationFileReader=MigrationFileReader.getInstance();

    private final Flyway flyway = Flyway.configure()
            .dataSource(PropertiesUtils.getJdbcUrl(),PropertiesUtils.getUsername(),PropertiesUtils.getPassword())
            .baselineOnMigrate(true)
            .load();

    public void saveMigrationRecord() {
        try {
            flyway.migrate();
        } catch (FlywayException e){
            logger.error("Failed to migrate {}",e.getMessage(),e);
        }
    }

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

        sqlScripts.sort((firstFile,secondFile)->Integer.compare(
                extractVersionFromFileName(firstFile.getName()),
                extractVersionFromFileName(secondFile.getName())));

        logger.info("sqlScripts files after sorting {}", sqlScripts.stream()
                .map(File::getName)
                .collect(Collectors.joining(", ")));

        sqlScripts.forEach((sqlScript) ->{
            saveMigrationRecord();
        });
    }

}
