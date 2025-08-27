# Stripe Payment Integration Guide

## Overview
This guide explains how to use the Stripe payment integration implemented in the Coachera e-learning platform. The integration provides a complete payment solution for course purchases with webhook support for real-time payment status updates.

## Features
- ✅ Create payment intents for course purchases
- ✅ Process payments securely with Stripe
- ✅ Real-time payment status updates via webhooks
- ✅ Payment history tracking
- ✅ Test environment support
- ✅ Comprehensive error handling

## Setup Instructions

### 1. Stripe Account Setup
1. Create a Stripe account at [stripe.com](https://stripe.com)
2. Get your API keys from the Stripe Dashboard
3. Set up webhook endpoints (see webhook section below)

### 2. Environment Configuration
Add the following properties to your `application.properties`:

```properties
# Stripe Configuration
stripe.secret.key=sk_test_your_test_secret_key_here
stripe.webhook.secret=whsec_your_webhook_secret_here
```

**Important**: 
- Use `sk_test_...` keys for testing
- Use `sk_live_...` keys for production
- Never commit real API keys to version control

### 3. Database Migration
The payment integration will automatically create the `payments` table when you start the application (using `spring.jpa.hibernate.ddl-auto=update`).

## API Endpoints

### 1. Create Payment Intent
**POST** `/api/payments/create-payment-intent`

Creates a payment intent for course purchase.

**Request Body:**
```json
{
  "courseId": 1,
  "amount": 99.99,
  "currency": "USD",
  "description": "Payment for Advanced Java Course"
}
```

**Response:**
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
    "description": "Payment for Advanced Java Course",
    "createdAt": "2024-01-15T10:30:00Z",
    "courseTitle": "Advanced Java Course",
    "studentName": "John Doe"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 2. Confirm Payment
**POST** `/api/payments/confirm/{paymentIntentId}`

Confirms a payment and updates its status.

### 3. Get Payment History
**GET** `/api/payments/my-payments`

Returns the current user's payment history.

### 4. Get Payment by ID
**GET** `/api/payments/{paymentId}`

Returns payment details by internal ID.

### 5. Get Payment by Stripe ID
**GET** `/api/payments/stripe/{stripePaymentIntentId}`

Returns payment details by Stripe payment intent ID.

## Testing with Stripe Test Cards

### Test Card Numbers
Use these test card numbers for different scenarios:

| Card Number | Brand | Description |
|-------------|-------|-------------|
| `4242424242424242` | Visa | Successful payment |
| `4000002500003155` | Visa | Requires authentication |
| `4000000000000002` | Visa | Declined payment |
| `4000000000009995` | Visa | Insufficient funds |
| `4000000000009987` | Visa | Lost card |
| `4000000000009979` | Visa | Stolen card |
| `4000000000000069` | Visa | Expired card |
| `4000000000000127` | Visa | Incorrect CVC |
| `4000000000000119` | Visa | Processing error |

### Test CVC and Expiry
- **CVC**: Any 3 digits (e.g., `123`)
- **Expiry**: Any future date (e.g., `12/25`)

### Testing Different Scenarios

#### 1. Successful Payment
```bash
curl -X POST http://localhost:8080/api/payments/create-payment-intent \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=your_session_id" \
  -d '{
    "courseId": 1,
    "amount": 99.99,
    "currency": "USD",
    "description": "Test payment"
  }'
```

#### 2. Test with Frontend Integration
```javascript
// Using Stripe.js
const stripe = Stripe('pk_test_your_publishable_key');
const elements = stripe.elements();

// Create payment element
const paymentElement = elements.create('payment');
paymentElement.mount('#payment-element');

// Handle form submission
const form = document.getElementById('payment-form');
form.addEventListener('submit', async (event) => {
  event.preventDefault();
  
  const {error} = await stripe.confirmPayment({
    elements,
    confirmParams: {
      return_url: 'https://your-domain.com/success',
    },
  });
  
  if (error) {
    console.error('Payment failed:', error);
  }
});
```

## Webhook Setup

### 1. Configure Webhook Endpoint
1. Go to Stripe Dashboard → Developers → Webhooks
2. Add endpoint: `https://your-domain.com/api/webhooks/stripe`
3. Select events: `payment_intent.succeeded`, `payment_intent.payment_failed`, `payment_intent.canceled`
4. Copy the webhook signing secret

### 2. Local Testing with Stripe CLI
```bash
# Install Stripe CLI
# Download from: https://stripe.com/docs/stripe-cli

# Login to Stripe
stripe login

# Forward webhooks to local server
stripe listen --forward-to localhost:8080/api/webhooks/stripe
```

### 3. Test Webhook Events
```bash
# Trigger test webhook
stripe trigger payment_intent.succeeded
```

## Error Handling

### Common Errors and Solutions

1. **Invalid API Key**
   - Ensure you're using the correct test/live keys
   - Check that the key is properly set in `application.properties`

2. **Webhook Signature Verification Failed**
   - Verify webhook secret is correct
   - Ensure webhook endpoint is accessible
   - Check that the request includes the `Stripe-Signature` header

3. **Payment Intent Creation Failed**
   - Verify amount is in the correct currency unit (cents for USD)
   - Check that course and student exist in the database
   - Ensure user is authenticated

4. **Database Connection Issues**
   - Verify PostgreSQL is running
   - Check database credentials in `application.properties`

## Security Best Practices

1. **Never expose secret keys** in client-side code
2. **Always verify webhook signatures** before processing events
3. **Use HTTPS** in production for all payment-related requests
4. **Implement proper authentication** for payment endpoints
5. **Log payment events** for audit purposes
6. **Handle failed payments gracefully** with user-friendly error messages

## Production Deployment

### 1. Switch to Live Keys
- Replace test keys with live keys in production environment
- Update webhook endpoints to production URLs
- Test thoroughly in Stripe's test mode first

### 2. SSL Certificate
- Ensure your domain has a valid SSL certificate
- Stripe requires HTTPS for webhook endpoints

### 3. Monitoring
- Set up monitoring for payment failures
- Monitor webhook delivery status
- Track payment success rates

## Support and Troubleshooting

### Stripe Resources
- [Stripe Documentation](https://stripe.com/docs)
- [Stripe Test Cards](https://stripe.com/docs/testing#cards)
- [Stripe Webhooks](https://stripe.com/docs/webhooks)

### Application Logs
Check application logs for detailed error information:
```bash
tail -f coachera.log
```

### Common Issues
1. **CORS errors**: Ensure proper CORS configuration for frontend integration
2. **Session issues**: Verify user authentication and session management
3. **Database constraints**: Check foreign key relationships between entities

## Testing Checklist

- [ ] Create payment intent with valid course and student
- [ ] Test successful payment with test card `4242424242424242`
- [ ] Test failed payment with test card `4000000000000002`
- [ ] Verify webhook events update payment status
- [ ] Test payment history retrieval
- [ ] Verify error handling for invalid requests
- [ ] Test authentication requirements
- [ ] Verify database records are created correctly

## Next Steps

1. **Frontend Integration**: Implement Stripe Elements in your frontend
2. **Email Notifications**: Add email confirmations for successful payments
3. **Refund Handling**: Implement refund functionality if needed
4. **Analytics**: Add payment analytics and reporting
5. **Multi-currency**: Support additional currencies if required 