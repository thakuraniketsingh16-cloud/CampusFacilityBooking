if (-not (Test-Path "bin\campus\Main.class")) {
    & .\compile.ps1
}
java -cp bin campus.Main $args