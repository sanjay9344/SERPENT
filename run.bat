@echo off
REM ==========================================================
REM SnakeGuard - Launch Script (Windows Batch)
REM Runs Spring Boot application on port 8080
REM ==========================================================

echo Starting SnakeGuard (Stage 1)...

if exist "C:\Users\sanjay\.maven\apache-maven-3.9.9\bin\mvn.cmd" (
    call "C:\Users\sanjay\.maven\apache-maven-3.9.9\bin\mvn.cmd" spring-boot:run
) else (
    call mvn spring-boot:run
)

pause
