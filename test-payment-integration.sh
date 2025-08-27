#!/bin/bash

# Payment Integration Test Script
# This script helps you test the Stripe payment integration

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
BASE_URL="http://localhost:8080"
COOKIE_FILE="test_cookies.txt"

echo -e "${BLUE}🎯 Stripe Payment Integration Test Script${NC}"
echo "================================================"

# Function to print colored output
print_status() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

# Check if application is running
check_application() {
    print_info "Checking if application is running..."
    
    if curl -s "$BASE_URL/actuator/health" > /dev/null 2>&1; then
        print_status "Application is running"
    else
        print_error "Application is not running. Please start it first:"
        echo "  mvn spring-boot:run"
        exit 1
    fi
}

# Clean up previous test data
cleanup() {
    print_info "Cleaning up previous test data..."
    rm -f "$COOKIE_FILE"
}

# Create test user
create_test_user() {
    print_info "Creating test user..."
    
    local response=$(curl -s -X POST "$BASE_URL/api/auth/register" \
        -H "Content-Type: application/json" \
        -d '{
            "username": "teststudent",
            "email": "teststudent@example.com",
            "password": "password123",
            "role": "student"
        }')
    
    if echo "$response" | grep -q "success"; then
        print_status "Test user created successfully"
    else
        print_warning "User might already exist or creation failed"
    fi
}

# Login and get session
login_user() {
    print_info "Logging in test user..."
    
    curl -s -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d '{
            "username": "teststudent",
            "password": "password123"
        }' \
        -c "$COOKIE_FILE" > /dev/null
    
    if [ -f "$COOKIE_FILE" ]; then
        print_status "Login successful, session saved"
    else
        print_error "Login failed"
        exit 1
    fi
}

# Test payment intent creation
test_payment_intent() {
    print_info "Testing payment intent creation..."
    
    local response=$(curl -s -X POST "$BASE_URL/api/payments/create-payment-intent" \
        -H "Content-Type: application/json" \
        -b "$COOKIE_FILE" \
        -d '{
            "courseId": 1,
            "amount": 99.99,
            "currency": "USD",
            "description": "Test payment for course"
        }')
    
    if echo "$response" | grep -q "success"; then
        print_status "Payment intent created successfully"
        echo "$response" | jq '.' 2>/dev/null || echo "$response"
    else
        print_error "Payment intent creation failed"
        echo "$response"
        exit 1
    fi
}

# Test payment history
test_payment_history() {
    print_info "Testing payment history retrieval..."
    
    local response=$(curl -s -X GET "$BASE_URL/api/payments/my-payments" \
        -H "Content-Type: application/json" \
        -b "$COOKIE_FILE")
    
    if echo "$response" | grep -q "success"; then
        print_status "Payment history retrieved successfully"
    else
        print_warning "Payment history retrieval failed or empty"
        echo "$response"
    fi
}

# Test error handling
test_error_handling() {
    print_info "Testing error handling..."
    
    # Test unauthenticated request
    local response=$(curl -s -X POST "$BASE_URL/api/payments/create-payment-intent" \
        -H "Content-Type: application/json" \
        -d '{
            "courseId": 1,
            "amount": 99.99,
            "currency": "USD"
        }')
    
    if echo "$response" | grep -q "unauthorized\|not authenticated"; then
        print_status "Authentication check working correctly"
    else
        print_warning "Authentication check might not be working"
    fi
    
    # Test invalid course ID
    local response=$(curl -s -X POST "$BASE_URL/api/payments/create-payment-intent" \
        -H "Content-Type: application/json" \
        -b "$COOKIE_FILE" \
        -d '{
            "courseId": 999,
            "amount": 99.99,
            "currency": "USD"
        }')
    
    if echo "$response" | grep -q "not found\|error"; then
        print_status "Invalid course ID handling working correctly"
    else
        print_warning "Invalid course ID handling might not be working"
    fi
}

# Show test cards
show_test_cards() {
    print_info "Stripe Test Cards for Frontend Testing:"
    echo ""
    echo "✅ Successful Payment: 4242424242424242"
    echo "❌ Declined Payment:   4000000000000002"
    echo "🔐 Requires Auth:      4000002500003155"
    echo "💰 Insufficient Funds: 4000000000009995"
    echo "🔒 Lost Card:          4000000000009987"
    echo "🚫 Stolen Card:        4000000000009979"
    echo "⏰ Expired Card:       4000000000000069"
    echo "🔢 Wrong CVC:          4000000000000127"
    echo "⚡ Processing Error:   4000000000000119"
    echo ""
    echo "CVC: 123, Expiry: 12/25"
}

# Main test flow
main() {
    echo ""
    print_info "Starting payment integration tests..."
    echo ""
    
    # Check prerequisites
    check_application
    
    # Clean up
    cleanup
    
    # Run tests
    create_test_user
    login_user
    test_payment_intent
    test_payment_history
    test_error_handling
    
    echo ""
    print_status "All tests completed!"
    echo ""
    
    # Show test cards
    show_test_cards
    
    echo ""
    print_info "Next steps:"
    echo "1. Test with the HTML file provided in the guide"
    echo "2. Set up webhooks for real-time updates"
    echo "3. Integrate with your frontend application"
    echo ""
    print_info "For detailed instructions, see: TESTING_GUIDE.md"
}

# Run main function
main "$@" 