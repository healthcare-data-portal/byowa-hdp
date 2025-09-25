# PowerShell Test Script for Authentication API

$BASE_URL = "http://localhost:8080/api/auth"

Write-Host "Testing Authentication API..." -ForegroundColor Green
Write-Host "==============================" -ForegroundColor Green

# Test 1: Register a new user
Write-Host "1. Testing user registration..." -ForegroundColor Yellow
$registerBody = @{
    username = "testuser"
    password = "testpass123"
} | ConvertTo-Json

try {
    $registerResponse = Invoke-RestMethod -Uri "$BASE_URL/register" -Method POST -Body $registerBody -ContentType "application/json"
    Write-Host "Register Response: $($registerResponse | ConvertTo-Json)" -ForegroundColor Cyan
} catch {
    Write-Host "Register Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 2: Login with the registered user
Write-Host "2. Testing user login..." -ForegroundColor Yellow
$loginBody = @{
    username = "testuser"
    password = "testpass123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$BASE_URL/login" -Method POST -Body $loginBody -ContentType "application/json"
    Write-Host "Login Response: $($loginResponse | ConvertTo-Json)" -ForegroundColor Cyan
} catch {
    Write-Host "Login Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 3: Try to register with existing username
Write-Host "3. Testing duplicate username registration..." -ForegroundColor Yellow
$duplicateBody = @{
    username = "testuser"
    password = "anotherpass"
} | ConvertTo-Json

try {
    $duplicateResponse = Invoke-RestMethod -Uri "$BASE_URL/register" -Method POST -Body $duplicateBody -ContentType "application/json"
    Write-Host "Duplicate Registration Response: $($duplicateResponse | ConvertTo-Json)" -ForegroundColor Cyan
} catch {
    Write-Host "Duplicate Registration Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 4: Try to login with wrong password
Write-Host "4. Testing login with wrong password..." -ForegroundColor Yellow
$wrongPassBody = @{
    username = "testuser"
    password = "wrongpass"
} | ConvertTo-Json

try {
    $wrongPassResponse = Invoke-RestMethod -Uri "$BASE_URL/login" -Method POST -Body $wrongPassBody -ContentType "application/json"
    Write-Host "Wrong Password Response: $($wrongPassResponse | ConvertTo-Json)" -ForegroundColor Cyan
} catch {
    Write-Host "Wrong Password Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

Write-Host "Tests completed!" -ForegroundColor Green
