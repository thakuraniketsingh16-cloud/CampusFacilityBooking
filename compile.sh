#!/usr/bin/env bash
echo "Compiling Campus Facility & Lab Booking System..."
mkdir -p bin
javac -d bin $(find src -name "*.java")
if [ $? -eq 0 ]; then
    echo "[SUCCESS] Compilation successful! Class files generated in bin/"
else
    echo "[ERROR] Compilation failed."
fi