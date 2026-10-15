# ==========================================================
# SnakeGuard - Launch Script (PowerShell)
# Runs Spring Boot application on port 8080
# ==========================================================

Write-Host "Starting SnakeGuard (Stage 1)..." -ForegroundColor Cyan

$mavenPath = "C:\Users\sanjay\.maven\apache-maven-3.9.9\bin\mvn.cmd"
if (Test-Path $mavenPath) {
    & $mavenPath spring-boot:run
} else {
    mvn spring-boot:run
}
