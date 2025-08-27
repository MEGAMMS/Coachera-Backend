# Complete Testing Guide for Stripe Payment Integration

## 🎯 Overview
This guide will walk you through testing the entire Stripe payment integration step by step, from initial setup to end-to-end payment processing.

## 📋 Prerequisites
- Java 21+ installed
- Maven installed
- PostgreSQL running
- Stripe account (free)
- Stripe CLI (optional, for webhook testing)

---

## 🚀 Step 1: Stripe Account Setup

### 1.1 Create Stripe Account
1. Go to [stripe.com](https://stripe.com)
2. Click "Start now" and create a free account
3. Complete the basic setup (no credit card required for testing)

### 1.2 Get API Keys
1. Go to Stripe Dashboard → Developers → API keys
2. Copy your **Publishable key** (starts with `pk_test_`)
3. Copy your **Secret key** (starts with `sk_test_`)
4. Keep these keys safe - you'll need them for testing

### 1.3 Set Up Webhook (Optional for Local Testing)
1. Go to Stripe Dashboard → Developers → Webhooks
2. Click "Add endpoint"
3. For local testing, you can skip this step initially
4. For production: Add `https://your-domain.com/api/webhooks/stripe`

---

## ⚙️ Step 2: Application Configuration

### 2.1 Add Stripe Properties
Add these lines to your `src/main/resources/application.properties`:

```properties
# Stripe Configuration
stripe.secret.key=sk_test_your_actual_secret_key_here
stripe.webhook.secret=whsec_your_webhook_secret_here
```

**Replace with your actual keys from Step 1.2**

### 2.2 Verify Database Configuration
Ensure your database is running and the application can connect:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/coachera
spring.datasource.username=coachera
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
```

---

## 🏃‍♂️ Step 3: Start the Application

### 3.1 Build and Run
```bash
# Navigate to your project directory
cd /home/mega/projects/Spring/Coachera

# Clean and build the project
mvn clean install

# Start the application
mvn spring-boot:run
```

### 3.2 Verify Application Started
1. Check the console output for any errors
2. Look for: "Started CoacheraApplication in X seconds"
3. The application should be running on `http://localhost:8080`

### 3.3 Check Database Tables
The `payments` table should be automatically created. You can verify this by:
1. Connecting to your PostgreSQL database
2. Running: `\dt payments;` (should show the payments table)

---

## 🧪 Step 4: Create Test Data

### 4.1 Create a Test User (Student)
First, you need to register a user and create a student profile. Use your existing registration endpoint:

```bash
# Register a new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "teststudent",
    "email": "teststudent@example.com",
    "password": "password123",
    "role": "student"
  }'
```

### 4.2 Create a Test Course
You'll need a course to purchase. Use your existing course creation endpoint or create one via your application interface.

### 4.3 Login to Get Session
```bash
# Login to get a session
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "teststudent",
    "password": "password123"
  }' \
  -c cookies.txt
```

**Save the session cookie for subsequent requests.**

---

## 💳 Step 5: Test Payment Creation

### 5.1 Create a Payment Intent
```bash
# Create a payment intent for course purchase
curl -X POST http://localhost:8080/api/payments/create-payment-intent \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "courseId": 1,
    "amount": 99.99,
    "currency": "USD",
    "description": "Payment for Test Course"
  }'
```

**Expected Response:**
```json
{
  "status": 200,
  "message": "Payment intent created successfully",
  "data": {
    "id": 1,
    "stripePaymentIntentId": "pi_1234567890",
    "clientSecret": "pi_1234567890_secret_abc123",
    "amount": 99.99,
    "currency": "USD",
    "status": "PENDING",
    "description": "Payment for Test Course",
    "createdAt": "2024-01-15T10:30:00Z",
    "courseTitle": "Test Course",
    "studentName": "John Doe"
  }
}
```

### 5.2 Verify Database Record
Check that the payment was saved to the database:
```bash
# Get payment by ID
curl -X GET http://localhost:8080/api/payments/1 \
  -H "Content-Type: application/json"
```

---

## 🧪 Step 6: Test Different Payment Scenarios

### 6.1 Test Successful Payment
Use Stripe's test card for successful payments:

**Card Number:** `4242424242424242`
**CVC:** `123`
**Expiry:** `12/25`

### 6.2 Test Failed Payment
Use Stripe's test card for declined payments:

**Card Number:** `4000000000000002`
**CVC:** `123`
**Expiry:** `12/25`

### 6.3 Test Authentication Required
Use Stripe's test card that requires authentication:

**Card Number:** `4000002500003155`
**CVC:** `123`
**Expiry:** `12/25`

---

## 🌐 Step 7: Frontend Integration Testing

### 7.1 Create a Simple HTML Test Page
Create a file called `payment-test.html`:

```html
<!DOCTYPE html>
<html>
<head>
    <title>Payment Test</title>
    <script src="https://js.stripe.com/v3/"></script>
</head>
<body>
    <h1>Payment Test</h1>
    
    <form id="payment-form">
        <div id="payment-element"></div>
        <button id="submit">Pay now</button>
        <div id="error-message"></div>
    </form>

    <script>
        // Replace with your publishable key
        const stripe = Stripe('pk_test_your_publishable_key_here');
        
        // Get client secret from your backend
        const clientSecret = 'pi_1234567890_secret_abc123'; // Replace with actual client secret
        
        const elements = stripe.elements({
            clientSecret: clientSecret
        });
        
        const paymentElement = elements.create('payment');
        paymentElement.mount('#payment-element');
        
        const form = document.getElementById('payment-form');
        form.addEventListener('submit', async (event) => {
            event.preventDefault();
            
            const {error} = await stripe.confirmPayment({
                elements,
                confirmParams: {
                    return_url: window.location.origin + '/success',
                },
            });
            
            if (error) {
                const messageDiv = document.getElementById('error-message');
                messageDiv.textContent = error.message;
            }
        });
    </script>
</body>
</html>
```

### 7.2 Test Frontend Integration
1. Replace `pk_test_your_publishable_key_here` with your actual publishable key
2. Replace the client secret with the one from your payment intent response
3. Open the HTML file in a browser
4. Test with different card numbers

---

## 🔄 Step 8: Webhook Testing (Optional)

### 8.1 Install Stripe CLI
```bash
# Download from: https://stripe.com/docs/stripe-cli
# Or use package manager:
# macOS: brew install stripe/stripe-cli/stripe
# Windows: scoop install stripe
```

### 8.2 Login to Stripe CLI
```bash
stripe login
```

### 8.3 Forward Webhooks to Local Server
```bash
stripe listen --forward-to localhost:8080/api/webhooks/stripe
```

### 8.4 Test Webhook Events
```bash
# Trigger a test payment succeeded event
stripe trigger payment_intent.succeeded

# Trigger a test payment failed event
stripe trigger payment_intent.payment_failed
```

### 8.5 Verify Webhook Processing
Check your application logs for webhook processing messages:
```bash
tail -f coachera.log
```

---

## 📊 Step 9: Payment History Testing

### 9.1 Get User's Payment History
```bash
curl -X GET http://localhost:8080/api/payments/my-payments \
  -H "Content-Type: application/json" \
  -b cookies.txt
```

### 9.2 Get Specific Payment
```bash
curl -X GET http://localhost:8080/api/payments/1 \
  -H "Content-Type: application/json"
```

### 9.3 Get Payment by Stripe ID
```bash
curl -X GET http://localhost:8080/api/payments/stripe/pi_1234567890 \
  -H "Content-Type: application/json"
```

---

## 🧪 Step 10: Error Handling Testing

### 10.1 Test Invalid Course ID
```bash
curl -X POST http://localhost:8080/api/payments/create-payment-intent \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "courseId": 999,
    "amount": 99.99,
    "currency": "USD"
  }'
```

### 10.2 Test Unauthenticated Request
```bash
curl -X POST http://localhost:8080/api/payments/create-payment-intent \
  -H "Content-Type: application/json" \
  -d '{
    "courseId": 1,
    "amount": 99.99,
    "currency": "USD"
  }'
```

### 10.3 Test Invalid Amount
```bash
curl -X POST http://localhost:8080/api/payments/create-payment-intent \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "courseId": 1,
    "amount": -10,
    "currency": "USD"
  }'
```

---

## ✅ Step 11: Verification Checklist

### 11.1 Database Verification
- [ ] `payments` table exists
- [ ] Payment records are created with correct data
- [ ] Payment status updates correctly
- [ ] Foreign key relationships work properly

### 11.2 API Verification
- [ ] Payment intent creation works
- [ ] Payment confirmation works
- [ ] Payment history retrieval works
- [ ] Error handling works for invalid requests
- [ ] Authentication requirements are enforced

### 11.3 Stripe Integration Verification
- [ ] Payment intents are created in Stripe Dashboard
- [ ] Test payments process correctly
- [ ] Webhook events are received (if configured)
- [ ] Payment statuses sync between Stripe and database

### 11.4 Security Verification
- [ ] Secret keys are not exposed in responses
- [ ] Webhook signatures are verified (if configured)
- [ ] Authentication is required for payment operations
- [ ] Input validation works correctly

---

## 🐛 Step 12: Troubleshooting

### 12.1 Common Issues and Solutions

#### Issue: "Invalid API Key"
**Solution:**
- Verify your API key is correct
- Ensure you're using test keys for testing
- Check that the key is properly set in `application.properties`

#### Issue: "Course not found"
**Solution:**
- Ensure the course exists in the database
- Verify the course ID is correct
- Check that the course is accessible

#### Issue: "Student not found"
**Solution:**
- Ensure the user is logged in
- Verify the session is valid
- Check that the user has a student profile

#### Issue: "Webhook signature verification failed"
**Solution:**
- Verify webhook secret is correct
- Ensure webhook endpoint is accessible
- Check that the request includes the `Stripe-Signature` header

#### Issue: "Database connection failed"
**Solution:**
- Verify PostgreSQL is running
- Check database credentials
- Ensure the database exists

### 12.2 Debug Mode
Enable debug logging by adding to `application.properties`:
```properties
logging.level.com.coachera.backend=DEBUG
logging.level.com.stripe=DEBUG
```

### 12.3 Check Application Logs
```bash
tail -f coachera.log
```

---

## 🎉 Step 13: Success Criteria

Your payment integration is working correctly if:

1. ✅ Payment intents are created successfully
2. ✅ Test payments process without errors
3. ✅ Payment records are saved to the database
4. ✅ Payment status updates correctly
5. ✅ Payment history is retrievable
6. ✅ Error handling works for invalid requests
7. ✅ Authentication is properly enforced
8. ✅ Webhook events are processed (if configured)

---

## 🚀 Step 14: Next Steps

Once testing is complete:

1. **Frontend Integration**: Implement the payment form in your actual frontend
2. **Production Setup**: Switch to live Stripe keys for production
3. **Monitoring**: Set up monitoring for payment failures
4. **Analytics**: Add payment analytics and reporting
5. **Email Notifications**: Add email confirmations for successful payments

---

## 📞 Support

If you encounter issues:

1. Check the application logs: `tail -f coachera.log`
2. Verify Stripe Dashboard for payment status
3. Check database records directly
4. Review the `PAYMENT_INTEGRATION_GUIDE.md` for detailed documentation
5. Consult Stripe documentation: [stripe.com/docs](https://stripe.com/docs)

---

**Happy Testing! 🎯** 