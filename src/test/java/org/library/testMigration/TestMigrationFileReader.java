package org.library.testMigration;

import org.junit.jupiter.api.*;
import org.library.migration.MigrationFileReader;

import java.io.File;
import java.util.List;

/**
 * Unit test for class MigrationFileReader
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestMigrationFileReader {

    private MigrationFileReader migrationFileReader=MigrationFileReader.getInstance();
    //you can change that on your path
    private final static String TEST_PATH="D:/InnowiseTasks/migration-library/src/test/testfiles";

    @Test
    @DisplayName("Test reading files from resources")
    public void TestReadFilesFromResources() {

        List<File> migrationFiles= migrationFileReader.readFilesFromResources('V');
        Assertions.assertNotNull(migrationFiles);
        Assertions.assertEquals(migrationFiles.size(),5);

        List<File> undoFiles= migrationFileReader.readFilesFromResources('U');
        Assertions.assertNotNull(undoFiles);
        Assertions.assertEquals(undoFiles.size(),4);

    }

    @Test
    @DisplayName("Test reading files from path")
    public void TestReadFilesFromOtherPaths() {

        List<File> migrationFiles= migrationFileReader
                .readFilesFromExternalDirectory (TEST_PATH,'V');
        Assertions.assertNotNull(migrationFiles);
        Assertions.assertEquals(migrationFiles.size(),2);

        List<File> undoFiles= migrationFileReader.readFilesFromExternalDirectory (TEST_PATH,'U');
        Assertions.assertNotNull(undoFiles);
        Assertions.assertEquals(undoFiles.size(),2);

    }

}
