@echo off

set PG_DIR=D:\programmes\PostgreSQL\9.2\bin

set PSQL=%PG_DIR%\psql.exe
set DROPDB=%PG_DIR%\dropdb.exe

echo debut

%DROPDB% -U postgres

%PSQL% -U postgres -f ./dropDatabase.sql
echo BDD supprimee

%PSQL% -U postgres -f ./creationDatabase.sql
echo creationDatabase

%PSQL% -U postgres -f ./creationTables.sql ocr
echo creationTables

%PSQL% -U postgres -f ./insertion_param.sql ocr
echo insertion_param

%PSQL% -U postgres -f ./insertion_famille.sql ocr
echo insertion_famille

%PSQL% -U postgres -f ./insertion_genre.sql ocr
echo insertion_genre

%PSQL% -U postgres -f ./insertion_espece.sql ocr
echo insertion_espece

%PSQL% -U postgres -f ./insertion_residus.sql ocr
echo insertion_residus

pause