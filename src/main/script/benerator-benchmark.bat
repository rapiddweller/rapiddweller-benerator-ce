@echo off
CALL "%~dp0benerator_common.bat" %*

if "%_JAVACMD%"=="" goto end

if "%_USE_CLASSPATH%"=="no" goto runNoClasspath
if not "%CLASSPATH%"=="" goto runWithClasspath

:runNoClasspath
"%_JAVACMD%" %BENERATOR_OPTS% -classpath "%LOCALCLASSPATH%" com.rapiddweller.benerator.main.Benchmark %*
goto end

:runWithClasspath
"%_JAVACMD%" %BENERATOR_OPTS% -classpath "%CLASSPATH%;%LOCALCLASSPATH%" com.rapiddweller.benerator.main.Benchmark %*
goto end

:end
set _JAVACMD=
set BENERATOR_CMD_LINE_ARGS=
