@echo off
chcp 65001 >nul
cd /d "%~dp0"
echo === Dang build va chay ung dung Phong kham Nha khoa (JavaFX) ===
call mvn -q compile
if errorlevel 1 (
    echo [LOI] Build that bai. Kiem tra JDK 21 va ket noi SQL Server.
    pause
    exit /b 1
)
call mvn -q javafx:run
if errorlevel 1 (
    echo [LOI] App ket thuc loi.
)
pause
