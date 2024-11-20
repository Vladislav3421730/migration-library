package org.migration;



import org.migration.migration.MigrationManager;
import org.migration.migration.MigrationTool;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
//        System.out.println(migrationManager.getAllVersions());
//
//        MigrationTool migrationTool= new MigrationTool();
//        migrationTool.executeAllMigrations();

        MigrationManager migrationManager=MigrationManager.getInstance();
        MigrationTool migrationTool=new MigrationTool();

        migrationTool.executeAllMigrations();

        migrationManager.rollback("2");

    }
}
