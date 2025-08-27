package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.PaymentRequestDTO;
import com.coachera.backend.dto.PaymentResponseDTO;
import com.coachera.backend.entity.User;
import com.coachera.backend.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment management APIs")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping("/create-payment-intent")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Create a payment intent for course purchase")
    public ApiResponse<PaymentResponseDTO> createPaymentIntent(
            @Valid @RequestBody PaymentRequestDTO request,
            @AuthenticationPrincipal User user) {
        
        PaymentResponseDTO response = paymentService.createPaymentIntent(request, user.getStudent().getId());
        return ApiResponse.success("Payment intent created successfully", response);
    }
    
    @PostMapping("/confirm/{paymentIntentId}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Confirm a payment")
    public ApiResponse<PaymentResponseDTO> confirmPayment(
            @PathVariable String paymentIntentId,
            @AuthenticationPrincipal User user) {
        
        PaymentResponseDTO response = paymentService.confirmPayment(paymentIntentId);
        return ApiResponse.success("Payment confirmed", response);
    }
    
    @GetMapping("/my-payments")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get current user's payment history")
    public ApiResponse<List<PaymentResponseDTO>> getMyPayments(@AuthenticationPrincipal User user) {
        List<PaymentResponseDTO> payments = paymentService.getStudentPayments(user.getStudent().getId());
        return ApiResponse.success("Payments retrieved successfully", payments);
    }
    
    @GetMapping("/{paymentId}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get payment by ID")
    public ApiResponse<PaymentResponseDTO> getPaymentById(
            @PathVariable Long paymentId,
            @AuthenticationPrincipal User user) {
        PaymentResponseDTO payment = paymentService.getPaymentById(paymentId);
        return ApiResponse.success("Payment retrieved successfully", payment);
    }
    
    @GetMapping("/stripe/{stripePaymentIntentId}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get payment by Stripe payment intent ID")
    public ApiResponse<PaymentResponseDTO> getPaymentByStripeId(
            @PathVariable String stripePaymentIntentId,
            @AuthenticationPrincipal User user) {
        PaymentResponseDTO payment = paymentService.getPaymentByStripeId(stripePaymentIntentId);
        return ApiResponse.success("Payment retrieved successfully", payment);
    }
} 