package migration.example;


import org.junit.jupiter.api.*;
import org.migration.migration.MigrationManager;
import org.migration.migration.MigrationTool;

import java.util.List;

/**
 * Unit test for migration-library.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppTest
{

    private final MigrationManager migrationManager=MigrationManager.getInstance();

    @Test
    @DisplayName("Test for generating tables in db. If table already exist, you don't need run this test")
    @Order(1)
    public void generateTableWithVersions(){
        MigrationTool migrationTool=new MigrationTool();
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
        migrationManager.rollback();
        Assertions.assertEquals(migrationManager.getLastVersion(), "4");
    }

    @Test
    @DisplayName("Test roll back to some migration")
    @Order(5)
    public void testRollbackSomeVersion(){
        migrationManager.rollbackToVersionByResources("2");
        Assertions.assertEquals(migrationManager.getLastVersion(), "2");
    }







}
