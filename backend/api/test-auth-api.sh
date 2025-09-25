#!/bin/bash

# Test script for the authentication API

BASE_URL="http://localhost:8080/api/auth"

echo "Testing Authentication API..."
echo "=============================="

# Test 1: Register a new user
echo "1. Testing user registration..."
register_response=$(curl -s -X POST \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "testpass123"}' \
  $BASE_URL/register)

echo "Register Response: $register_response"
echo ""

# Test 2: Login with the registered user
echo "2. Testing user login..."
login_response=$(curl -s -X POST \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "testpass123"}' \
  $BASE_URL/login)

echo "Login Response: $login_response"
echo ""

# Test 3: Try to register with existing username
echo "3. Testing duplicate username registration..."
duplicate_response=$(curl -s -X POST \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "anotherpass"}' \
  $BASE_URL/register)

echo "Duplicate Registration Response: $duplicate_response"
echo ""

# Test 4: Try to login with wrong password
echo "4. Testing login with wrong password..."
wrong_pass_response=$(curl -s -X POST \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "wrongpass"}' \
  $BASE_URL/login)

echo "Wrong Password Response: $wrong_pass_response"
echo ""

echo "Tests completed!"
