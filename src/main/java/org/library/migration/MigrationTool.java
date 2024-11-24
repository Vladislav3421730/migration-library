package org.library.migration;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.library.utils.FileVersion.extractVersionFromFileName;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MigrationTool {

    private static final MigrationTool INSTANCE = new MigrationTool();
    public static MigrationTool getInstance() {
        return INSTANCE;
    }

    private final MigrationFileReader migrationFileReader = MigrationFileReader.getInstance();
    private final MigrationExecutor migrationExecutor = MigrationExecutor.getInstance();

    /**
     * Method to execute all migration files located
     * in resources in the db/migration folder
     */
    public void executeAllMigrations() {
        execute(migrationFileReader.readFilesFromResources('V'));
    }

    /**
     * Method to execute all migration files located in the specified folder
     * @param filePath Provided folder
     */
    public void executeAllMigrations(String filePath) {
        execute(migrationFileReader.readFilesFromExternalDirectory(filePath,'V'));
    }

    private void execute(List<File> sqlScripts) {

        log.info("Trying sorting sqlScripts files {}", sqlScripts.stream()
                .map(File::getName)
                .collect(Collectors.joining(", ")));

        sqlScripts.sort(Comparator.comparing(firstFile -> extractVersionFromFileName(firstFile.getName())));

        log.info("sqlScripts files after sorting {}", sqlScripts.stream()
                .map(File::getName)
                .collect(Collectors.joining(", ")));

        migrationExecutor.executeSqlScript(sqlScripts);
    }

}
