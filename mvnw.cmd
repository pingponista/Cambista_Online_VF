@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM ----------------------------------------------------------------------------

@if "%DEBUG%" == "" @echo off
@setlocal

set ERROR_CODE=0

@REM Find JAVA_HOME
if not "%JAVA_HOME%" == "" goto OkJHome
echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.
echo.
goto error

:OkJHome
set DEFAULT_MAVEN_USER_HOME=%USERPROFILE%\.m2
if "%MAVEN_USER_HOME%" == "" set MAVEN_USER_HOME=%DEFAULT_MAVEN_USER_HOME%

set MAVEN_WRAPPER_JAR="%~dp0.mvn\wrapper\maven-wrapper.jar"
set MAVEN_WRAPPER_PROPERTIES="%~dp0.mvn\wrapper\maven-wrapper.properties"

java -jar "%~dp0.mvn\wrapper\maven-wrapper.jar" %*
if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%
exit /B %ERROR_CODE%
