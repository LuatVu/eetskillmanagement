@echo off
setlocal EnableDelayedExpansion

set MYSQL_HOME="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
set host_name=localhost
set user=skm_admin
set password=CpmxbvIf4#PZMYmBL
set max_error_count=0

%MYSQL_HOME% -h %host_name% -u %user% --password=%password% < sql_script/schema.sql
%MYSQL_HOME% -h %host_name% -u %user% --password=%password% < sql_script/create_connection_user.sql

%MYSQL_HOME% -h %host_name% -u %user% --password=%password% < sql_script/update_schema.sql
%MYSQL_HOME% -h %host_name% -u %user% --password=%password% < sql_script/update_data.sql

%MYSQL_HOME% -h %host_name% -u %user% --password=%password% < sql_script/oauth2_schema.sql
%MYSQL_HOME% -h %host_name% -u %user% --password=%password% < sql_script/oauth2_data.sql

%MYSQL_HOME% -h %host_name% -u %user% --password=%password% < sql_script/predefined_data.sql
%MYSQL_HOME% -h %host_name% -u %user% --password=%password% < sql_script/predefined_roles_permissions.sql
%MYSQL_HOME% -h %host_name% -u %user% --password=%password% < sql_script/predefined_users.sql

%MYSQL_HOME% -h %host_name% -u %user% --password=%password% < sql_script/generate_predefined_data.sql

if NOT ["%errorlevel%"]==["0"] (
	echo Database installation is failed.
	pause
	exit /b %errorlevel%
) else (
	echo Database installation is success.
	pause
)
