@echo off
REM Build script for Y Language Project on Windows

setlocal enabledelayedexpansion

echo 🚀 Building Y Language Project...

REM Check if required tools are installed
echo [INFO] Checking dependencies...

where java >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Java is not installed. Please install Java 17 or higher.
    exit /b 1
)

where node >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Node.js is not installed. Please install Node.js 18 or higher.
    exit /b 1
)

where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Maven is not installed. Please install Maven 3.6 or higher.
    exit /b 1
)

echo [INFO] All dependencies are installed.

REM Build backend
echo [INFO] Building backend...
cd backend

echo [INFO] Cleaning and compiling...
call mvn clean compile
if %errorlevel% neq 0 (
    echo [ERROR] Backend compilation failed.
    exit /b 1
)

echo [INFO] Running tests...
call mvn test
if %errorlevel% neq 0 (
    echo [WARNING] Some tests failed.
)

echo [INFO] Packaging...
call mvn package -DskipTests
if %errorlevel% neq 0 (
    echo [ERROR] Backend packaging failed.
    exit /b 1
)

cd ..
echo [INFO] Backend build completed successfully.

REM Build frontend
echo [INFO] Building frontend...
cd frontend

echo [INFO] Installing dependencies...
call npm install
if %errorlevel% neq 0 (
    echo [ERROR] Frontend dependency installation failed.
    exit /b 1
)

echo [INFO] Running tests...
call npm test -- --passWithNoTests
if %errorlevel% neq 0 (
    echo [WARNING] Some frontend tests failed.
)

echo [INFO] Building...
call npm run build
if %errorlevel% neq 0 (
    echo [ERROR] Frontend build failed.
    exit /b 1
)

cd ..
echo [INFO] Frontend build completed successfully.

echo ✅ Build completed successfully!
echo.
echo You can now start the services:
echo   - Backend: cd backend && mvn spring-boot:run
echo   - Frontend: cd frontend && npm run dev
echo.
echo Or use Docker:
echo   - docker-compose up --build

endlocal