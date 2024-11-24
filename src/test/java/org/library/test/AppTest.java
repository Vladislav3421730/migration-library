package org.library.test;


import org.junit.jupiter.api.*;
import org.library.migration.MigrationManager;
import org.library.migration.MigrationRollBackManager;
import org.library.migration.MigrationTool;

import java.util.List;

/**
 * Unit test for migration-library.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppTest
{

    private final MigrationManager migrationManager=MigrationManager.getInstance();
    private final MigrationRollBackManager migrationRollBackManager=MigrationRollBackManager.getInstance();

    @Test
    @DisplayName("Test for generating tables in db. If table already exist, you don't need run this test")
    @Order(1)
    public void generateTableWithVersions(){
        MigrationTool migrationTool=MigrationTool.getInstance();
        migrationTool.executeAllMigrations();
    }

    @Test
    @DisplayName("Test current version from db")
    @Order(2)
    public void testCurrentVersion(){
        Assertions.assertEquals(migrationManager.getLastVersion(),"5");
    }

    @Test
    @DisplayName("Test all versions from db")
    @Order(3)
    public void testVersions(){
        Assertions.assertEquals(migrationManager.getAllVersions(), List.of("1","2","3","4","5"));
    }

    @Test
    @DisplayName("Test roll back to previous migration")
    @Order(4)
    public void testRollbackOneVersion(){
        migrationRollBackManager.rollback();
        Assertions.assertEquals(migrationManager.getLastVersion(), "4");
    }

    @Test
    @DisplayName("Test roll back to some migration")
    @Order(5)
    public void testRollbackSomeVersion(){
        migrationRollBackManager.rollbackToVersionByResources("2");
        Assertions.assertEquals(migrationManager.getLastVersion(), "2");
    }

}
