**just as a small demo
should be reworked with proper logic for frontend
to be edited with proper logic n stuff**

# HDP Authentication API

Simple Spring Boot authentication API with PostgreSQL database.

## What it does

- Register new users with username and password
- Login users and return JWT tokens
- Store user data in PostgreSQL database

## How to run

1. Make sure Docker is running
2. Run: docker-compose up --build
3. API will be available at http://localhost:8080

## API Endpoints

POST /api/auth/register
- Body: {"username": "yourname", "password": "yourpass"}
- Returns: {"token": "jwt_token_here"}

POST /api/auth/login  
- Body: {"username": "yourname", "password": "yourpass"}
- Returns: {"token": "jwt_token_here"}

## Test the API

Run the PowerShell test script:
./test-auth-api.ps1

Or use PowerShell commands:
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method POST -Body '{"username": "test", "password": "test123"}' -ContentType "application/json"

## Database

PostgreSQL runs on localhost:5432
Database: hdp_auth
Username: postgres  
Password: password



