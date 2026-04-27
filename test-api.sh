#!/bin/bash

# Spring Boot AI Integration Demo - API Test Script
# This script demonstrates the three main use cases
# Make sure the application is running on http://localhost:8080

BASE_URL="http://localhost:8080/api"

echo "====================================="
echo "Spring Boot AI Integration Demo"
echo "====================================="
echo ""

# Test 1: Product Search WITHOUT AI Enhancement
echo "Test 1: Product Search (WITHOUT AI)"
echo "-----------------------------------"
curl -s "$BASE_URL/products/search?query=laptop&useAIEnhancement=false" | jq .
echo ""
echo ""

# Test 2: Product Search WITH AI Enhancement  
echo "Test 2: Product Search (WITH AI Enhancement)"
echo "---------------------------------------------"
curl -s "$BASE_URL/products/search?query=laptop&useAIEnhancement=true" | jq .
echo ""
echo ""

# Test 3: Create Support Ticket (with AI auto-response)
echo "Test 3: Create Support Ticket (AI Auto-Response)"
echo "-----------------------------------------------"
curl -s -X POST "$BASE_URL/support/tickets" \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Jane Smith",
    "email": "jane@example.com",
    "issue": "I cannot login to my account, getting a timeout error"
  }' | jq .
echo ""
echo ""

# Test 4: Get Personalized Recommendations
echo "Test 4: Personalized Recommendations (AI-Powered)"
echo "-------------------------------------------------"
curl -s "$BASE_URL/recommendations?userId=1&limit=5" | jq .
echo ""
echo ""

# Test 5: Complex Product Search with POST
echo "Test 5: Complex Product Search with POST"
echo "----------------------------------------"
curl -s -X POST "$BASE_URL/products/search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "professional audio equipment",
    "category": "Audio",
    "maxPrice": 500,
    "useAIEnhancement": true
  }' | jq .
echo ""

echo "====================================="
echo "Demo completed!"
echo "====================================="
