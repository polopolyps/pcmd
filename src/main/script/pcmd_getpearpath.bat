rem search processes
WMIC /OUTPUT:pcmd_tempfile.txt PROCESS where "name='java.exe' AND Commandline like '%%pear%%'" get Commandline

rem change utf8 to ascii
TYPE "pcmd_tempfile.txt" > "pcmd_tempfile1.txt"

rem remove header by filter
FINDSTR /C:"pear" pcmd_tempfile1.txt > pcmd_tempfile2.txt

rem get first line
SET PCMP_TEMP_STRING=
FOR /F "usebackq tokens=1 delims=," %%A IN ("pcmd_tempfile2.txt") DO (
	SET PCMP_TEMP_STRING=%%A
	goto endLoop1
)
:endLoop1

rem replace 'pear' with 'pear,'','
set PCMP_TEMP_STRING=%PCMP_TEMP_STRING:pear=pear,%
rem keep result in file
del /Q pcmd_tempfile2.txt
echo %PCMP_TEMP_STRING% >> pcmd_tempfile2.txt

rem get only string before ','
FOR /F "usebackq tokens=1 delims=," %%A IN ("pcmd_tempfile2.txt") DO (
	SET PCMP_TEMP_STRING=%%A
	goto endLoop1
)
:endLoop1

rem replace / with \
set PCMP_TEMP_STRING2=%PCMP_TEMP_STRING:/=\%

SET PCMP_VAR_DRIVE=c:\
:tryAnotherDrive
rem replace drive letter (ex c:\) with + sign.
if "%PCMP_VAR_DRIVE%" == "c:\" (
	rem echo try c:\
	set PCMP_TEMP_STRING2=%PCMP_TEMP_STRING2:c:\=+%
)
if "%PCMP_VAR_DRIVE%" == "d:\" (
	rem echo try d:\
	set PCMP_TEMP_STRING2=%PCMP_TEMP_STRING2:d:\=+%
)
if "%PCMP_VAR_DRIVE%" == "e:\" (
	rem echo try e:\
	set PCMP_TEMP_STRING2=%PCMP_TEMP_STRING2:e:\=+%
)
if "%PCMP_VAR_DRIVE%" == "f:\" (
	rem echo try f:\
	set PCMP_TEMP_STRING2=%PCMP_TEMP_STRING2:f:\=+%
)

del /Q pcmd_tempfile2.txt

rem it can have multiple + sign (because of multiple c:\), so split this in 20 lines. 
FOR /F "tokens=1-20 delims=+" %%a IN ("%PCMP_TEMP_STRING2%") DO (
	echo on
	echo %%a >> pcmd_tempfile2.txt
	echo %%b >> pcmd_tempfile2.txt
	echo %%c >> pcmd_tempfile2.txt
	echo %%d >> pcmd_tempfile2.txt
	echo %%e >> pcmd_tempfile2.txt
	
	echo %%f >> pcmd_tempfile2.txt
	echo %%g >> pcmd_tempfile2.txt
	echo %%h >> pcmd_tempfile2.txt
	echo %%i >> pcmd_tempfile2.txt
	echo %%j >> pcmd_tempfile2.txt
	
	echo %%k >> pcmd_tempfile2.txt
	echo %%l >> pcmd_tempfile2.txt
	echo %%m >> pcmd_tempfile2.txt
	echo %%n >> pcmd_tempfile2.txt	
	echo %%o >> pcmd_tempfile2.txt
	
	echo %%p >> pcmd_tempfile2.txt
	echo %%q >> pcmd_tempfile2.txt
	echo %%r >> pcmd_tempfile2.txt
	echo %%s >> pcmd_tempfile2.txt
	echo %%t >> pcmd_tempfile2.txt
	echo off
)

rem filter only lines which have 'pear'
FINDSTR /C:"pear" pcmd_tempfile2.txt > pcmd_tempfile1.txt

rem get only the first line
FOR /F "usebackq tokens=* delims=;" %%i IN ("pcmd_tempfile1.txt") DO (
	SET PCMP_TEMP_STRING2=%%i
)

rem check whether the first line is really a correct folder
IF NOT EXIST "%PCMP_VAR_DRIVE%%PCMP_TEMP_STRING2%" (
	rem if the first line is not correct folder, try other drive
	if "%PCMP_VAR_DRIVE%" == "c:\" (
		IF EXIST "d:\" (
			SET PCMP_VAR_DRIVE=d:\
			goto tryAnotherDrive
		)
	)
	
	if "%PCMP_VAR_DRIVE%" == "d:\" (
		IF EXIST "e:\" (
			SET PCMP_VAR_DRIVE=e:\
			goto tryAnotherDrive
		)
	)
	if "%PCMP_VAR_DRIVE%" == "e:\" (
		IF EXIST "f:\" (
			SET PCMP_VAR_DRIVE=f:\
			goto tryAnotherDrive
		)
	)
	
	rem if it has tried all c: till f: and still cannot find the PEAR path. Then check whether there is a setting in PCMD_PEAR_PATH
	IF NOT EXIST "%PCMD_PEAR_PATH%" (
		rem if nothing in PCMD_PEAR_PATH, ask from the user
		SET /p PCMD_PEAR_PATH_LOCAL="We cannot find the PEAR path which is supposed to be running. If you did not start the Polopoly, please start it, then key the PEAR path in here(ex c:\polopoly\pear\):"
		SET PCMD_IS_ASK_FOR_SET=true

		goto endOfFile
	) else (
		rem if there is a setting in PCMD_PEAR_PATH, use it.
		SET PCMD_PEAR_PATH_LOCAL=%PCMD_PEAR_PATH%
		goto endOfFile
	)
) else (
	rem echo find PEAR!
)

SET PCMD_PEAR_PATH_LOCAL=%PCMP_VAR_DRIVE%%PCMP_TEMP_STRING2%

SET PCMD_PEAR_PATH_LOCAL=%PCMD_PEAR_PATH_LOCAL: =\%

:endOfFile
rem if the path has been gotten by 'asking', remember this value for next time.
IF NOT "%PCMD_IS_ASK_FOR_SET%" == "true" goto notSet
set PCMD_PEAR_PATH=%PCMD_PEAR_PATH_LOCAL%
set PCMD_IS_ASK_FOR_SET=
:notSet

del /Q pcmd_tempfile.txt
del /Q pcmd_tempfile1.txt
del /Q pcmd_tempfile2.txt
