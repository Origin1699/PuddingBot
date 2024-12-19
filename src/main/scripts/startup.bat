@echo off
setlocal

set "JAVA=%JAVA_HOME%\bin\java.exe"
set "APP_NAME=pudding-bot"

cd /d "%~dp0"
set "APP_HOME=%cd%"

set "JAVA_OPTS=%JAVA_OPTS% -server -Djava.net.preferIPv4Stack=true -Duser.timezone=Asia/Shanghai -Dclient.encoding.override=UTF-8"
set "JAVA_OPTS=%JAVA_OPTS% -Xms1024m -Xmx2048m -XX:+HeapDumpOnOutOfMemoryError"
set "JAVA_OPTS=%JAVA_OPTS% -Dloader.path=%APP_HOME%\lib"

rem 找到 pudding-bot.jar 文件
for %%F in ("%APP_HOME%\*.jar") do set "BOOT_JAR=%%F"

echo Starting %APP_NAME%...
start "" "%JAVA%" %JAVA_OPTS% -jar "%BOOT_JAR%" %*

endlocal
