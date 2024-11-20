package org.migration;



import org.migration.migration.MigrationTool;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        MigrationTool migrationTool=new MigrationTool();
        migrationTool.executeAllMigrations();
    }
}
