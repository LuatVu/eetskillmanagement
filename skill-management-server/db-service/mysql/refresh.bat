@echo off
setlocal EnableDelayedExpansion

set MYSQL_HOME="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
set host_name=localhost
set user=skm_admin
set password=CpmxbvIf4#PZMYmBL
set max_error_count=0

%MYSQL_HOME% -h %host_name% -u %user% --password=%password% < sql_script/refresh.sql

if NOT ["%errorlevel%"]==["0"] (
	echo Database installation is failed.
	pause
	exit /b %errorlevel%
) else (
	echo Database installation is success.
	pause
)
