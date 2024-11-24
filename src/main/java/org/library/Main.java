package org.library;

import org.library.migration.MigrationManager;
import org.library.migration.MigrationRollBackManager;
import org.library.migration.MigrationTool;

import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Class for demonstrating the work of the library
 */
public class Main {
    /**
     * Simple console app for demonstrat
     * @param args
     * @throws FileNotFoundException  {@link FileNotFoundException} if some files were not found
     */
    public static void main(String[] args) throws FileNotFoundException {

        MigrationTool migrationTool = MigrationTool.getInstance();
       migrationTool.executeAllMigrations();
        MigrationManager migrationManager = MigrationManager.getInstance();
        MigrationRollBackManager migrationRollBackManager = MigrationRollBackManager.getInstance();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nSelect an action:");
            System.out.println("1. View the current version");
            System.out.println("2. View all versions");
            System.out.println("3. View the last report");
            System.out.println("4. View all reports");
            System.out.println("5. Rollback the last migration");
            System.out.println("6. Rollback to a specific migration");
            System.out.println("7. Exit");
            System.out.print("Enter the action number: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Viewing the current version...");
                    System.out.println(migrationManager.getLastVersion());
                    break;

                case 2:
                    System.out.println("Viewing all versions...");
                    System.out.println(migrationManager.getAllVersions());
                    break;

                case 3:
                    System.out.println("Viewing the last report...");
                    System.out.println(migrationManager.getLastMigrationReport());
                    break;

                case 4:
                    System.out.println("Viewing all reports...");
                    System.out.println(migrationManager.getAllMigrationReports());
                    break;

                case 5:
                    System.out.println("Rolling back the last migration...");
                    migrationRollBackManager.rollback();
                    break;

                case 6:
                    System.out.print("Enter the version number to rollback to: ");
                    String version = scanner.next();
                    System.out.println("Rolling back to version " + version + "...");
                    migrationRollBackManager.rollbackToVersionByResources(version);
                    break;

                case 7:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
