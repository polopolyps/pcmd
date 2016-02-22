@echo off
echo off

SET PCMD_CURRENT_DIR=%~dp0

rem hot fix
call pcmd_getpearpath.bat
SETLOCAL ENABLEDELAYEDEXPANSION 

SET PCMD_OPTION=
SET PCMD_CLIENT_LIBS=

IF NOT "" == "%POLOPOLY_SERVER%" (
	SET PCMD_OPTION=%PCMD_OPTION% --server=%POLOPOLY_SERVER%
)

IF NOT "" == "%POLOPOLY_USER%" (
	SET PCMD_OPTION=%PCMD_OPTION% --user=%POLOPOLY_USER%
)

IF NOT "" == "%POLOPOLY_PASSWORD%" (
	SET PCMD_OPTION=%PCMD_OPTION% --password=%POLOPOLY_PASSWORD%
)

IF EXIST "%PCMD_CURRENT_DIR%..\custom-lib\*.jar" (
	FOR /F "tokens=*" %%G IN ('dir /b %PCMD_CURRENT_DIR%..\custom-lib\*.jar') DO (
		SET PCMD_CLIENT_LIBS=!PCMD_CLIENT_LIBS!%PCMD_CURRENT_DIR%..\custom-lib\%%G;
	)
)

IF EXIST "%PCMD_CURRENT_DIR%..\lib\*.jar" (
	FOR /F "tokens=*" %%G IN ('dir /b %PCMD_CURRENT_DIR%..\lib\*.jar') DO (
		SET PCMD_CLIENT_LIBS=!PCMD_CLIENT_LIBS!%PCMD_CURRENT_DIR%..\lib\%%G;
	)
)

IF EXIST "%PCMD_PEAR_PATH_LOCAL%..\custom\client-lib\*.jar" (
	FOR /F "tokens=*" %%G IN ('dir /b %PCMD_PEAR_PATH_LOCAL%..\custom\client-lib\*.jar') DO (
		SET PCMD_CLIENT_LIBS=!PCMD_CLIENT_LIBS!%PCMD_PEAR_PATH_LOCAL%..\custom\client-lib\%%G;
	)
)

IF EXIST "%PCMD_PEAR_PATH_LOCAL%jar-repository\policy-lib\*.jar" (
	FOR /F "tokens=*" %%G IN ('dir /b %PCMD_PEAR_PATH_LOCAL%jar-repository\policy-lib\*.jar') DO (
		SET PCMD_CLIENT_LIBS=!PCMD_CLIENT_LIBS!%PCMD_PEAR_PATH_LOCAL%jar-repository\policy-lib\%%G;
	)
)

set LOCALCLASSPATH=%PCMD_CURRENT_DIR%..\lib\*;%PCMD_PEAR_PATH_LOCAL%..\custom\client-lib\*;%CLASSPATH%;

IF "%1" == "--verbose" (
	echo PCMD_PEAR_PATH_LOCAL:%PCMD_PEAR_PATH_LOCAL%
	echo JAVA_OPTS:%JAVA_OPTS% 
	echo PCMD_CLIENT_LIBS:%PCMD_CLIENT_LIBS% 
	echo PCMD_OPTION:%PCMD_OPTION% 
	echo LOCALCLASSPATH:%LOCALCLASSPATH%
	echo "Full classpath consists of: %PCMD_CLIENT_LIBS%"
)

java %JAVA_OPTS% -cp %LOCALCLASSPATH% com.polopoly.ps.pcmd.Main%PCMD_OPTION% %*
