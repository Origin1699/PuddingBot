#!/bin/bash

# 应用程序名称
APP_NAME="pudding-bot"
APP_HOME=$(cd $(dirname "$0") && pwd)

# 日志文件夹和 PID 文件
LOG_FOLDER="${APP_HOME}/logs"
PID_FILE="${LOG_FOLDER}/${APP_NAME}.pid"

# 检查是否有 PID 文件存在
if [ ! -f "$PID_FILE" ]; then
    echo "No PID file found for $APP_NAME. Is the application running?"
    exit 1
fi

# 读取 PID 文件中的进程 ID
PID=$(cat "$PID_FILE")

# 检查进程是否在运行
if ps -p "$PID" > /dev/null; then
    echo "Stopping $APP_NAME (PID: $PID)..."
    # 发送终止信号
    kill "$PID"

    # 检查是否成功停止进程
    if [ $? -eq 0 ]; then
        # 删除 PID 文件
        rm -f "$PID_FILE"
        echo "$APP_NAME stopped successfully."
    else
        echo "Failed to stop $APP_NAME. You may need to stop it manually."
        exit 1
    fi
else
    echo "No running process found for PID $PID. Removing stale PID file."
    # 删除过时的 PID 文件
    rm -f "$PID_FILE"
fi
