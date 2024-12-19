@echo off
setlocal

set "PATH=%JAVA_HOME%\bin;%PATH%"

echo Killing pudding-bot server...

rem 查找运行中的 pudding-bot 进程并结束
for /f "tokens=1" %%i in ('jps -m ^| findstr "pudding-bot"') do (
    taskkill /F /PID %%i
)

echo Done!

endlocal
