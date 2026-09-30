@echo off
title Rahul Stores Supermarket Billing Launcher
cd /d "%~dp0"

echo ========================================================
echo   Launching Rahul Stores Supermarket Billing System
echo ========================================================
echo.

:: Check if port 8085 is already listening
netstat -ano | findstr /R /C:":8085 .*LISTENING" >nul 2>&1
if %ERRORLEVEL% equ 0 (
    echo [OK] Supermarket Billing server is already running!
    echo Opening browser at http://localhost:8085 ...
    start http://localhost:8085
    exit /b 0
)

echo [*] Starting Spring Boot Server...
start "Rahul Stores Server" /min cmd /c "mvn spring-boot:run"

echo [*] Waiting for server to initialize...
:wait_loop
timeout /t 2 /nobreak >nul
netstat -ano | findstr /R /C:":8085 .*LISTENING" >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [*] Still booting up...
    goto wait_loop
)

echo [OK] Server is ready!
echo Opening browser at http://localhost:8085 ...
start http://localhost:8085
exit /b 0
