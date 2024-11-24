package org.library;

import org.library.migration.MigrationManager;
import org.library.migration.MigrationRollBackManager;
import org.library.migration.MigrationTool;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        MigrationTool migrationTool = MigrationTool.getInstance();
        migrationTool.executeAllMigrations();
        MigrationManager migrationManager=MigrationManager.getInstance();
        MigrationRollBackManager migrationRollBackManager=MigrationRollBackManager.getInstance();


        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nВыберите действие:");
            System.out.println("1. Просмотреть текущую версию");
            System.out.println("2. Посмотреть все версии");
            System.out.println("3. Посмотреть последний отчёт");
            System.out.println("4. Посмотреть все отчёты");
            System.out.println("5. Откатить на одну миграцию назад");
            System.out.println("6. Откатиться к определённой миграции");
            System.out.println("7. Завершить работу");
            System.out.print("Введите номер действия: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Просмотр текущей версии...");
                    System.out.println(migrationManager.getLastVersion());
                    break;

                case 2:
                    System.out.println("Просмотр всех версий...");
                    System.out.println(migrationManager.getAllVersions());
                    break;

                case 3:
                    System.out.println("Просмотр последнего отчёта...");
                    System.out.println(migrationManager.getLastMigrationReport());
                    break;

                case 4:
                    System.out.println("Просмотр всех отчётов...");
                    System.out.println(migrationManager.getAllMigrationReports());
                    break;

                case 5:
                    System.out.println("Откат на одну миграцию назад...");
                    migrationRollBackManager.rollback();
                    break;

                case 6:
                    System.out.print("Введите номер версии для отката: ");
                    String version = scanner.next();
                    System.out.println("Откат к версии " + version + "...");
                    migrationRollBackManager.rollbackToVersionByResources(version);
                    break;

                case 7:
                    System.out.println("Завершение работы...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }



    }
}
