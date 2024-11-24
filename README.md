# migration-library
The library runs on java 17
You may to copy the project library
```bash
https://github.com/Vladislav3421730/migration-library
```
In class Main you can start simple ConsoleApp or start AppTest class, which contains 5 simple tests for checking basic functionality.
If you downloaded the library locally to your computer, you can view javadoc at the link
```bash
file:///your_disc:/your_folder/migration-library/javadoc/index.html
```
Or just open index.html from the explorer itself.
In order to use the library, you don't need to install the library locally, but simply download it `migration-library-1.0.0-jar-with-dependencies.jar`,which contains in jar folder <br>
After this you need to import the library. Just open `file->Project Structure->Libraries`. Then click on the plus sign above the Maven libraries and add  `migration-library-1.0.0-jar-with-dependencies.jar`, and then click `Apply`. After that you can freely use the library. In the root of the project, you can create a `jsonReports` folder to save reports in json format. 
## CLI utility
Unfortunately, only one method from the utility works: status, below I will explain why. To use it, go to make `cd jar` in library. After that use
```bash
java -cp migration-library-1.0.0-jar-with-dependencies.jar org.library.CLI status
```
---
Далее текст идёт на русском. Так легче рассказать, что было мною сделано, а что нет. В программе было выполнено<br>
* Выполнение миграций и сортировка файлов по версии
* Откат на одну версию назад
* Откат к определённой версии
* Реализованы поддрежка внешнего хранилища (через абсолютный путь директории, в которой содержатся .sql файлы). Проверку данного метода можно увидеть в тестах
* Реализован механизм блокировки для предотвращения конфликтов (при помощи lock)
* Логирование процесса
* Сохранение всех миграций в бд в таблице schema_history
* Написана javadoc документация
* Библиотеку можно лекго подключить в качесвто jar файла (я подключал библиотеку к другому проекту и всё работало)
* Генерирование отчётов в JSON формате в папке jsonReports (отчёты генерируются независимо от того, как прошла миграция)
* Написал небольшие Unit тесты. Для метода getLastVersion() писал при помощи Mockito.
* Сделана CLI утилила для одного метода status
Моменты, которые у меня не получилось реализовать и их причины
* Реализация поддержки миграций для популярных баз данных (PostgreSQL, MySQL, H2).
Почему-то mysql не поддерживает  connection.setAutoCommit(false). При тестах, если например в пятой версии ошибка, то транзанкия не откатывается полностью, хотя в Postgresql всё рабоает.
* Методы migrate и rollback для CLI утилиллы. Почему-то jar файл не видит .sql файлы, и с какими я бы плагинами не пробовал собрать проект, jar файл всё равно не видел .sql файлы.
Поэтому работает только метод status, где .sql файлы не требуются.
