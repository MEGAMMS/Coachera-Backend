package com.coachera.backend.controller;

import com.coachera.backend.service.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookController {
    
    private final PaymentService paymentService;
    
    @Value("${stripe.webhook.secret}")
    private String webhookSecret;
    
    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader,
            HttpServletRequest request) {
        
        Event event;
        
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.error("Invalid signature: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }
        
        log.info("Received Stripe webhook event: {}", event.getType());
        
        // Handle the event
        switch (event.getType()) {
            case "payment_intent.succeeded":
                PaymentIntent paymentIntent = (PaymentIntent) event.getData().getObject();
                log.info("Payment succeeded: {}", paymentIntent.getId());
                paymentService.confirmPayment(paymentIntent.getId());
                break;
                
            case "payment_intent.payment_failed":
                PaymentIntent failedPayment = (PaymentIntent) event.getData().getObject();
                log.info("Payment failed: {}", failedPayment.getId());
                paymentService.confirmPayment(failedPayment.getId());
                break;
                
            case "payment_intent.canceled":
                PaymentIntent canceledPayment = (PaymentIntent) event.getData().getObject();
                log.info("Payment canceled: {}", canceledPayment.getId());
                paymentService.confirmPayment(canceledPayment.getId());
                break;
                
            default:
                log.info("Unhandled event type: {}", event.getType());
        }
        
        return ResponseEntity.ok("Webhook processed successfully");
    }
} 