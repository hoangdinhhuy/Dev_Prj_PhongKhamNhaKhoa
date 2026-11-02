@echo off
chcp 65001 >nul
cd /d "%~dp0"
echo === Dang build va chay ung dung Phong kham Nha khoa ===
call mvn -q clean compile
if errorlevel 1 (
    echo [LOI] Build that bai. Kiem tra JDK 21 va ket noi SQL Server.
    pause
    exit /b 1
)
call mvn javafx:run
pause
