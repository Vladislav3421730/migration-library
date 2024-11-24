package org.library;

import lombok.extern.slf4j.Slf4j;
import org.library.migration.MigrationManager;
import org.library.migration.MigrationRollBackManager;
import org.library.migration.MigrationTool;


/**
 * Class for working with CLI utility
 */
public class CLI {
    /**
     * Method for running an application as a CLI utility
     * @param args Parameters entered in the console line
     */
    public static void main(String[] args) {

        MigrationRollBackManager migrationRollBackManager=MigrationRollBackManager.getInstance();
        MigrationTool migrationTool=MigrationTool.getInstance();
        MigrationManager migrationManager=MigrationManager.getInstance();

        switch (args[0]) {
            case "migrate" ->  migrationTool.executeAllMigrations();
            case "rollback" -> migrationRollBackManager.rollback();
            case "status" -> {
                System.out.println(migrationManager.getLastVersion());
                System.out.println(migrationManager.getAllMigrationReports());
            }
            default -> {
                System.out.println("Please, enter current operation: migrate, rollback,status");
            }
        }
    }
}
