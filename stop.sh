#!/bin/bash
PID_FILE="$(dirname "${BASH_SOURCE[0]}")/rentmis.pid"
if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    kill "$PID" 2>/dev/null && echo "RentMIS stopped (PID: $PID)" || echo "Process not running"
    rm "$PID_FILE"
else
    pkill -f "RentMIS-1.0.0.jar" && echo "RentMIS stopped" || echo "RentMIS not running"
fi
