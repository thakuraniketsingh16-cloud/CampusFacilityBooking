@echo off
echo ===================================================
echo Compiling Campus Facility and Lab Booking System...
echo ===================================================
if not exist bin mkdir bin
javac -d bin src\campus\models\*.java src\campus\exceptions\*.java src\campus\storage\*.java src\campus\service\*.java src\campus\*.java
if %ERRORLEVEL% equ 0 (
    echo [SUCCESS] Compilation finished. Class files generated in bin/
) else (
    echo [ERROR] Compilation failed. Please check JDK installation.
)