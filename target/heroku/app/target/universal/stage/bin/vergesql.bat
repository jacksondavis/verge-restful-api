@REM vergesql launcher script
@REM
@REM Environment:
@REM JAVA_HOME - location of a JDK home dir (optional if java on path)
@REM CFG_OPTS  - JVM options (optional)
@REM Configuration:
@REM VERGESQL_config.txt found in the VERGESQL_HOME.
@setlocal enabledelayedexpansion

@echo off
if "%VERGESQL_HOME%"=="" set "VERGESQL_HOME=%~dp0\\.."
set ERROR_CODE=0

set "APP_LIB_DIR=%VERGESQL_HOME%\lib\"

rem Detect if we were double clicked, although theoretically A user could
rem manually run cmd /c
for %%x in (%cmdcmdline%) do if %%~x==/c set DOUBLECLICKED=1

rem FIRST we load the config file of extra options.
set "CFG_FILE=%VERGESQL_HOME%\VERGESQL_config.txt"
set CFG_OPTS=
if exist %CFG_FILE% (
  FOR /F "tokens=* eol=# usebackq delims=" %%i IN ("%CFG_FILE%") DO (
    set DO_NOT_REUSE_ME=%%i
    rem ZOMG (Part #2) WE use !! here to delay the expansion of
    rem CFG_OPTS, otherwise it remains "" for this loop.
    set CFG_OPTS=!CFG_OPTS! !DO_NOT_REUSE_ME!
  )
)

rem We use the value of the JAVACMD environment variable if defined
set _JAVACMD=%JAVACMD%

if "%_JAVACMD%"=="" (
  if not "%JAVA_HOME%"=="" (
    if exist "%JAVA_HOME%\bin\java.exe" set "_JAVACMD=%JAVA_HOME%\bin\java.exe"
  )
)

if "%_JAVACMD%"=="" set _JAVACMD=java

rem Detect if this java is ok to use.
for /F %%j in ('"%_JAVACMD%" -version  2^>^&1') do (
  if %%~j==Java set JAVAINSTALLED=1
)

rem BAT has no logical or, so we do it OLD SCHOOL! Oppan Redmond Style
set JAVAOK=true
if not defined JAVAINSTALLED set JAVAOK=false

if "%JAVAOK%"=="false" (
  echo.
  echo A Java JDK is not installed or can't be found.
  if not "%JAVA_HOME%"=="" (
    echo JAVA_HOME = "%JAVA_HOME%"
  )
  echo.
  echo Please go to
  echo   http://www.oracle.com/technetwork/java/javase/downloads/index.html
  echo and download a valid Java JDK and install before running vergesql.
  echo.
  echo If you think this message is in error, please check
  echo your environment variables to see if "java.exe" and "javac.exe" are
  echo available via JAVA_HOME or PATH.
  echo.
  if defined DOUBLECLICKED pause
  exit /B 1
)


rem We use the value of the JAVA_OPTS environment variable if defined, rather than the config.
set _JAVA_OPTS=%JAVA_OPTS%
if "%_JAVA_OPTS%"=="" set _JAVA_OPTS=%CFG_OPTS%

rem We keep in _JAVA_PARAMS all -J-prefixed and -D-prefixed arguments
rem "-J" is stripped, "-D" is left as is, and everything is appended to JAVA_OPTS
set _JAVA_PARAMS=

:param_beforeloop
if [%1]==[] goto param_afterloop
set _TEST_PARAM=%~1

rem ignore arguments that do not start with '-'
if not "%_TEST_PARAM:~0,1%"=="-" (
  shift
  goto param_beforeloop
)

set _TEST_PARAM=%~1
if "%_TEST_PARAM:~0,2%"=="-J" (
  rem strip -J prefix
  set _TEST_PARAM=%_TEST_PARAM:~2%
)

if "%_TEST_PARAM:~0,2%"=="-D" (
  rem test if this was double-quoted property "-Dprop=42"
  for /F "delims== tokens=1-2" %%G in ("%_TEST_PARAM%") DO (
    if not "%%G" == "%_TEST_PARAM%" (
      rem double quoted: "-Dprop=42" -> -Dprop="42"
      set _JAVA_PARAMS=%%G="%%H"
    ) else if [%2] neq [] (
      rem it was a normal property: -Dprop=42 or -Drop="42"
      set _JAVA_PARAMS=%_TEST_PARAM%=%2
      shift
    )
  )
) else (
  rem a JVM property, we just append it
  set _JAVA_PARAMS=%_TEST_PARAM%
)

:param_loop
shift

if [%1]==[] goto param_afterloop
set _TEST_PARAM=%~1

rem ignore arguments that do not start with '-'
if not "%_TEST_PARAM:~0,1%"=="-" goto param_loop

set _TEST_PARAM=%~1
if "%_TEST_PARAM:~0,2%"=="-J" (
  rem strip -J prefix
  set _TEST_PARAM=%_TEST_PARAM:~2%
)

if "%_TEST_PARAM:~0,2%"=="-D" (
  rem test if this was double-quoted property "-Dprop=42"
  for /F "delims== tokens=1-2" %%G in ("%_TEST_PARAM%") DO (
    if not "%%G" == "%_TEST_PARAM%" (
      rem double quoted: "-Dprop=42" -> -Dprop="42"
      set _JAVA_PARAMS=%_JAVA_PARAMS% %%G="%%H"
    ) else if [%2] neq [] (
      rem it was a normal property: -Dprop=42 or -Drop="42"
      set _JAVA_PARAMS=%_JAVA_PARAMS% %_TEST_PARAM%=%2
      shift
    )
  )
) else (
  rem a JVM property, we just append it
  set _JAVA_PARAMS=%_JAVA_PARAMS% %_TEST_PARAM%
)
goto param_loop
:param_afterloop

set _JAVA_OPTS=%_JAVA_OPTS% %_JAVA_PARAMS%
:run
 
set "APP_CLASSPATH=%APP_LIB_DIR%\vergesql.vergesql-0.1.jar;%APP_LIB_DIR%\org.scala-lang.scala-compiler-2.11.6.jar;%APP_LIB_DIR%\org.scala-lang.scala-library-2.11.6.jar;%APP_LIB_DIR%\org.scala-lang.scala-reflect-2.11.6.jar;%APP_LIB_DIR%\org.squeryl.squeryl_2.11-0.9.6-RC3.jar;%APP_LIB_DIR%\cglib.cglib-nodep-2.2.jar;%APP_LIB_DIR%\org.scala-lang.scalap-2.11.1.jar;%APP_LIB_DIR%\postgresql.postgresql-8.4-701.jdbc4.jar;%APP_LIB_DIR%\org.apache.httpcomponents.httpclient-4.3.3.jar;%APP_LIB_DIR%\org.apache.httpcomponents.httpcore-4.3.2.jar;%APP_LIB_DIR%\commons-logging.commons-logging-1.1.3.jar;%APP_LIB_DIR%\commons-codec.commons-codec-1.6.jar;%APP_LIB_DIR%\org.apache.httpcomponents.httpclient-cache-4.3.3.jar;%APP_LIB_DIR%\org.apache.directory.studio.org.apache.commons.io-2.4.jar;%APP_LIB_DIR%\commons-io.commons-io-2.4.jar;%APP_LIB_DIR%\io.spray.spray-can_2.11-1.3.3.jar;%APP_LIB_DIR%\io.spray.spray-io_2.11-1.3.3.jar;%APP_LIB_DIR%\io.spray.spray-util_2.11-1.3.3.jar;%APP_LIB_DIR%\io.spray.spray-http_2.11-1.3.3.jar;%APP_LIB_DIR%\org.parboiled.parboiled-scala_2.11-1.1.7.jar;%APP_LIB_DIR%\org.parboiled.parboiled-core-1.1.7.jar;%APP_LIB_DIR%\io.spray.spray-routing_2.11-1.3.3.jar;%APP_LIB_DIR%\io.spray.spray-httpx_2.11-1.3.3.jar;%APP_LIB_DIR%\org.jvnet.mimepull.mimepull-1.9.5.jar;%APP_LIB_DIR%\com.chuusai.shapeless_2.11-1.2.4.jar;%APP_LIB_DIR%\com.typesafe.config-1.2.1.jar;%APP_LIB_DIR%\com.typesafe.play.play-json_2.11-2.3.4.jar;%APP_LIB_DIR%\com.typesafe.play.play-iteratees_2.11-2.3.4.jar;%APP_LIB_DIR%\org.scala-stm.scala-stm_2.11-0.7.jar;%APP_LIB_DIR%\com.typesafe.play.play-functional_2.11-2.3.4.jar;%APP_LIB_DIR%\com.typesafe.play.play-datacommons_2.11-2.3.4.jar;%APP_LIB_DIR%\joda-time.joda-time-2.3.jar;%APP_LIB_DIR%\org.joda.joda-convert-1.6.jar;%APP_LIB_DIR%\org.jsoup.jsoup-1.7.2.jar;%APP_LIB_DIR%\io.gatling.highcharts.gatling-charts-highcharts-2.1.6.jar;%APP_LIB_DIR%\io.gatling.gatling-charts-2.1.6.jar;%APP_LIB_DIR%\io.gatling.gatling-core-2.1.6.jar;%APP_LIB_DIR%\com.typesafe.akka.akka-actor_2.11-2.3.10.jar;%APP_LIB_DIR%\com.dongxiguo.fastring_2.11-0.2.4.jar;%APP_LIB_DIR%\net.sf.opencsv.opencsv-2.3.jar;%APP_LIB_DIR%\com.googlecode.concurrentlinkedhashmap.concurrentlinkedhashmap-lru-1.4.2.jar;%APP_LIB_DIR%\org.threeten.threetenbp-1.2.jar;%APP_LIB_DIR%\org.scala-lang.modules.scala-parser-combinators_2.11-1.0.4.jar;%APP_LIB_DIR%\com.ning.async-http-client-1.9.22.jar;%APP_LIB_DIR%\io.netty.netty-3.10.3.Final.jar;%APP_LIB_DIR%\org.slf4j.slf4j-api-1.7.12.jar;%APP_LIB_DIR%\com.typesafe.scala-logging.scala-logging_2.11-3.1.0.jar;%APP_LIB_DIR%\ch.qos.logback.logback-classic-1.1.3.jar;%APP_LIB_DIR%\ch.qos.logback.logback-core-1.1.3.jar;%APP_LIB_DIR%\io.gatling.jsonpath_2.11-0.6.4.jar;%APP_LIB_DIR%\com.fasterxml.jackson.core.jackson-databind-2.5.3.jar;%APP_LIB_DIR%\com.fasterxml.jackson.core.jackson-annotations-2.5.0.jar;%APP_LIB_DIR%\com.fasterxml.jackson.core.jackson-core-2.5.3.jar;%APP_LIB_DIR%\io.fastjson.boon-0.32.jar;%APP_LIB_DIR%\net.sf.saxon.Saxon-HE-9.6.0-5.jar;%APP_LIB_DIR%\org.jodd.jodd-lagarto-3.6.5.jar;%APP_LIB_DIR%\org.jodd.jodd-core-3.6.5.jar;%APP_LIB_DIR%\org.jodd.jodd-log-3.6.5.jar;%APP_LIB_DIR%\com.tdunning.t-digest-3.0.jar;%APP_LIB_DIR%\io.gatling.gatling-app-2.1.6.jar;%APP_LIB_DIR%\io.gatling.gatling-http-2.1.6.jar;%APP_LIB_DIR%\com.jcraft.jzlib-1.1.3.jar;%APP_LIB_DIR%\org.scala-lang.modules.scala-xml_2.11-1.0.4.jar;%APP_LIB_DIR%\io.gatling.gatling-jms-2.1.6.jar;%APP_LIB_DIR%\org.apache.geronimo.specs.geronimo-jms_1.1_spec-1.1.1.jar;%APP_LIB_DIR%\io.gatling.gatling-jdbc-2.1.6.jar;%APP_LIB_DIR%\io.gatling.gatling-redis-2.1.6.jar;%APP_LIB_DIR%\net.debasishg.redisclient_2.11-2.14.jar;%APP_LIB_DIR%\commons-pool.commons-pool-1.6.jar;%APP_LIB_DIR%\io.gatling.gatling-metrics-2.1.6.jar;%APP_LIB_DIR%\com.github.scopt.scopt_2.11-3.3.0.jar;%APP_LIB_DIR%\io.gatling.gatling-recorder-2.1.6.jar;%APP_LIB_DIR%\org.scala-lang.modules.scala-swing_2.11-1.0.2.jar;%APP_LIB_DIR%\org.bouncycastle.bcpkix-jdk15on-1.52.jar;%APP_LIB_DIR%\org.bouncycastle.bcprov-jdk15on-1.52.jar"
set "APP_MAIN_CLASS=vergedatabaseSQL.Boot"

rem Call the application and pass all arguments unchanged.
"%_JAVACMD%" %_JAVA_OPTS% %VERGESQL_OPTS% -cp "%APP_CLASSPATH%" %APP_MAIN_CLASS% %*
if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end

@endlocal

exit /B %ERROR_CODE%
