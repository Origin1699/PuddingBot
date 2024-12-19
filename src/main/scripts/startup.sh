#!/bin/bash

# 获取当前脚本的目录
PRG="$0"
PRG_DIR=$(dirname "$PRG")
APP_HOME=$(cd "$PRG_DIR" && pwd)

# 应用程序名称和 JAR 文件
APP_NAME="pudding-bot"
APP_JAR="$APP_HOME/${APP_NAME}.jar"

# 配置日志文件夹和 PID 文件
LOG_FOLDER="${APP_HOME}/logs"
LOG_FILENAME="${APP_NAME}.log"
PID_FILE="${LOG_FOLDER}/${APP_NAME}.pid"

# 创建日志目录
mkdir -p "$LOG_FOLDER"

# Java 启动参数
JAVA_OPTS="-Dlogging.file=$LOG_FOLDER/$LOG_FILENAME"
JAVA_OPTS="$JAVA_OPTS -Dloader.path=${APP_HOME}/lib"
JAVA_OPTS="$JAVA_OPTS -Xms512m -Xmx1024m -XX:+HeapDumpOnOutOfMemoryError"

# 打印启动日志
echo "Starting $APP_NAME..."

# 检查是否已有运行的实例
if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if ps -p "$PID" > /dev/null; then
        echo "Error: $APP_NAME is already running with PID $PID."
        exit 1
    else
        # 如果PID文件存在，但进程不存在，删除PID文件
        rm -f "$PID_FILE"
    fi
fi

# 启动应用程序
nohup java $JAVA_OPTS -jar "$APP_JAR" > "$LOG_FOLDER/startup.log" 2>&1 &

# 获取应用进程 ID 并存储到 PID 文件
PID=$!
echo "$PID" > "$PID_FILE"

if [ $? -eq 0 ]; then
    echo "$APP_NAME started successfully, PID: $PID"
else
    echo "Failed to start $APP_NAME."
    exit 1
fi
