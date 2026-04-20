#!/bin/bash
# ============================================================
# RentMIS Startup Script
# ============================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAR="$SCRIPT_DIR/target/RentMIS-1.0.0.jar"
LOG_DIR="$SCRIPT_DIR/logs"
PID_FILE="$SCRIPT_DIR/rentmis.pid"

mkdir -p "$LOG_DIR"

# Load .env
if [ -f "$SCRIPT_DIR/.env" ]; then
    export $(cat "$SCRIPT_DIR/.env" | grep -v '^#' | xargs)
fi

# Check MySQL
if ! service mysql status > /dev/null 2>&1; then
    echo "Starting MySQL..."
    service mysql start
    sleep 2
fi

# Kill existing process
if [ -f "$PID_FILE" ]; then
    OLD_PID=$(cat "$PID_FILE")
    kill "$OLD_PID" 2>/dev/null
    rm "$PID_FILE"
    sleep 1
fi

echo "Starting RentMIS on port ${APP_PORT:-5050}..."

nohup java \
    -Xms256m -Xmx512m \
    -jar "$JAR" \
    --spring.profiles.active=default \
    > "$LOG_DIR/rentmis.log" 2>&1 &

PID=$!
echo $PID > "$PID_FILE"

sleep 5
if kill -0 $PID 2>/dev/null; then
    echo "RentMIS started successfully (PID: $PID)"
    echo "Access: http://localhost:${APP_PORT:-5050}"
    echo "Login:  http://localhost:${APP_PORT:-5050}/login"
    echo "API:    http://localhost:${APP_PORT:-5050}/api"
    echo "Logs:   $LOG_DIR/rentmis.log"
else
    echo "Failed to start RentMIS. Check logs:"
    tail -30 "$LOG_DIR/rentmis.log"
    exit 1
fi
