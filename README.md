﻿# migration-library
На данный момент проект можно запустить, если у вас установлен PostgreSQL локально на компьютере (через Docker JDBC не видит БД).
Можно проверить работу программу при помощи тестов (класс AppTest) либо запустить консольное приложение в классе Main.
На данный момент в программе достпуен функционал <br>
***
Мграция всех SQL файлов, откат на одну миграцию назад, откат на к определённой версии миграции, получение версии миграции, получение всех версий миграции, 
блокировка одновременного запуска миграций (при помощи lock), генерация отчёта в json формате
при любом исходе миграции (неважно SUCCESS или FAILED), откат всех изменений при ошибке в миграции
***
Функционал, который на данный в разработке: <br>
**
Работа с несколькими базами данных, создание CLI утилиллы, миграция файлов с внешних источников (методы написаны, но пока не проверяд их)
**
