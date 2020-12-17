rem %~dp0 is expanded pathname of the current script under NT
set DEFAULT_BENERATOR_HOME=%~dp0..

if "%BENERATOR_HOME%"=="" set BENERATOR_HOME=%DEFAULT_BENERATOR_HOME%
set DEFAULT_BENERATOR_HOME=

set _USE_CLASSPATH=yes

rem Slurp the command line arguments. This loop allows for an unlimited number
rem of arguments (up to the command line limit, anyway).
set BENERATOR_CMD_LINE_ARGS=%1
if ""%1""=="""" goto doneStart
shift
:setupArgs
if ""%1""=="""" goto doneStart
if ""%1""==""-noclasspath"" goto clearclasspath
set BENERATOR_CMD_LINE_ARGS=%BENERATOR_CMD_LINE_ARGS% %1
shift
goto setupArgs

rem here is there is a -noclasspath in the options
:clearclasspath
set _USE_CLASSPATH=no
shift
goto setupArgs

rem This label provides a place for the argument list loop to break out
rem and for NT handling to skip to.

:doneStart
rem check the value of BENERATOR_HOME
if exist "%BENERATOR_HOME%\license.txt" goto setLocalClassPath

:noBeneratorHome
echo BENERATOR_HOME is set incorrectly. 
echo Please set the BENERATOR_HOME environment variable to the path where you installed benerator.
goto endcommon

:setLocalClassPath
set LOCALCLASSPATH=.;%BENERATOR_HOME%\bin;%BENERATOR_HOME%\lib\*
echo Local classpath: %LOCALCLASSPATH%

:checkJava
set _JAVACMD=%JAVACMD%

if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=%JAVA_HOME%\bin\java.exe
goto endcommon

:noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=java.exe

:endcommon
