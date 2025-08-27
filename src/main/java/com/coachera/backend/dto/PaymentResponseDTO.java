package com.coachera.backend.dto;

import com.coachera.backend.entity.Payment;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponseDTO {
    
    private Long id;
    private String stripePaymentIntentId;
    private String clientSecret;
    private BigDecimal amount;
    private String currency;
    private Payment.PaymentStatus status;
    private String description;
    private LocalDateTime createdAt;
    private String courseTitle;
    private String studentName;
    
    public static PaymentResponseDTO fromPayment(Payment payment, String clientSecret) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setId(payment.getId());
        dto.setStripePaymentIntentId(payment.getStripePaymentIntentId());
        dto.setClientSecret(clientSecret);
        dto.setAmount(payment.getAmount());
        dto.setCurrency(payment.getCurrency());
        dto.setStatus(payment.getStatus());
        dto.setDescription(payment.getDescription());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setCourseTitle(payment.getCourse().getTitle());
        dto.setStudentName(payment.getStudent().getFirstName() + " " + payment.getStudent().getLastName());
        return dto;
    }
} 