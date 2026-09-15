@echo off
if not exist bin\campus\Main.class (
    echo Project not compiled yet. Compiling now...
    call compile.bat
)
java -cp bin campus.Main %*