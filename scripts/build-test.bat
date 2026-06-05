@echo off
REM Test script for Journal App - Windows version

setlocal enabledelayedexpansion

echo ================================
echo Journal App Build Test Script
echo ================================

REM Step 1: Clean and compile
echo.
echo [1/6] Cleaning and compiling...
call mvn clean compile -DskipTests
if !errorlevel! neq 0 (
    echo [FAILED] Compilation failed
    exit /b 1
)
echo [PASSED] Compilation successful

REM Step 2: Run tests
echo.
echo [2/6] Running unit tests...
call mvn test
if !errorlevel! neq 0 (
    echo [FAILED] Tests failed
    exit /b 1
)
echo [PASSED] Tests passed

REM Step 3: Package application
echo.
echo [3/6] Packaging application...
call mvn package -DskipTests
if !errorlevel! neq 0 (
    echo [FAILED] Packaging failed
    exit /b 1
)
echo [PASSED] Package created

REM Step 4: Verify JAR exists
echo.
echo [4/6] Verifying JAR file...
if exist "target\journalApp-1.0.0.jar" (
    for %%A in (target\journalApp-1.0.0.jar) do set "size=%%~zA"
    echo [PASSED] JAR file exists (Size: !size! bytes)
) else (
    echo [FAILED] JAR file not found
    exit /b 1
)

REM Step 5: Display build info
echo.
echo [5/6] Build Information
echo Application: Journal App
echo Version: 1.0.0
echo Java: 17
echo Build Status: SUCCESS

echo.
echo [SUCCESS] All checks passed!
echo Ready for deployment!
echo.
echo Next steps:
echo   git add .
echo   git commit -m "Fix: GoogleAuthController import and add health checks"
echo   git push origin master

endlocal
