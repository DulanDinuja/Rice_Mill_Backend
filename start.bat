
@echo off
echo ========================================
echo Rice Mill Backend - Quick Start
echo ========================================
echo.

echo Checking if Docker is running...
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Docker is not running. Please start Docker Desktop.
    pause
    exit /b 1
)

echo Starting PostgreSQL...
docker-compose up -d

echo Waiting for PostgreSQL to be ready...
timeout /t 10 /nobreak >nul

echo.
echo Building application...
call mvnw.cmd clean install -DskipTests

if %errorlevel% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo Starting application...
echo.
echo Application will be available at:
echo   - API: http://localhost:8080/api
echo   - Swagger UI: http://localhost:8080/api/swagger-ui.html
echo.
echo Default credentials:
echo   Username: admin
echo   Password: admin123
echo.
echo Press Ctrl+C to stop the application
echo.

call mvnw.cmd spring-boot:run
