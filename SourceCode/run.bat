@echo off
setlocal
chcp 65001 >nul
cd /d "%~dp0"
set "CLINIC_MVN=mvn"
if defined MAVEN_HOME if exist "%MAVEN_HOME%\bin\mvn.cmd" set "CLINIC_MVN=%MAVEN_HOME%\bin\mvn.cmd"
if defined M2_HOME if exist "%M2_HOME%\bin\mvn.cmd" set "CLINIC_MVN=%M2_HOME%\bin\mvn.cmd"
if exist "%~dp0.tools\apache-maven-3.9.9\bin\mvn.cmd" set "CLINIC_MVN=%~dp0.tools\apache-maven-3.9.9\bin\mvn.cmd"
if "%CLINIC_MVN%"=="mvn" (
    where mvn >nul 2>nul
    if errorlevel 1 (
        echo [LOI] Khong tim thay Maven. Cai Maven va them vao PATH hoac dat MAVEN_HOME.
        if /I not "%~1"=="--check" pause
        exit /b 1
    )
)
echo === Dang build va chay ung dung Phong kham Nha khoa (JavaFX) ===
call "%CLINIC_MVN%" -q compile
if errorlevel 1 (
    echo [LOI] Build that bai. Kiem tra JDK 21 va thong bao loi phia tren.
    if /I not "%~1"=="--check" pause
    exit /b 1
)
if /I "%~1"=="--check" exit /b 0
call "%CLINIC_MVN%" -q javafx:run
if errorlevel 1 (
    echo [LOI] App ket thuc loi.
    pause
    exit /b 1
)
pause
exit /b 0
