@echo off
setlocal EnableDelayedExpansion

set MYSQL_HOME="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
set host_name=localhost
set user=skm_admin
set password=CpmxbvIf4#PZMYmBL
set max_error_count=0

%MYSQL_HOME% -h %host_name% -u %user% --password=%password% < ../sql_script/update_schema.sql
%MYSQL_HOME% -h %host_name% -u %user% --password=%password% < ../sql_script/update_data.sql

%MYSQL_HOME% -h %host_name% -u %user% --password=%password% < update_dev_data.sql
REM %MYSQL_HOME% -h %host_name% -u %user% --password=%password% < ../sql_script/test_data.sql
REM %MYSQL_HOME% -h %host_name% -u %user% --password=%password% < ../sql_script/test_data_dummy.sql

if NOT ["%errorlevel%"]==["0"] (
	echo Update Database is failed.
	pause
	exit /b %errorlevel%
) else (
	echo Update Database is success.
	pause
)
