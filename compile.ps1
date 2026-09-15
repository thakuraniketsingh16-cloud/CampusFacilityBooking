Write-Host "Compiling Campus Facility & Lab Booking System..." -ForegroundColor Cyan
if (-not (Test-Path "bin")) { New-Item -ItemType Directory -Path "bin" | Out-Null }
$sources = Get-ChildItem -Path src -Filter *.java -Recurse | Select-Object -ExpandProperty FullName
javac -d bin $sources
if ($LASTEXITCODE -eq 0) {
    Write-Host "[SUCCESS] Compilation successful! Files saved to bin/" -ForegroundColor Green
} else {
    Write-Host "[ERROR] Compilation failed." -ForegroundColor Red
}