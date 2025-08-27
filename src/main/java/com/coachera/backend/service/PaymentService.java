package com.coachera.backend.service;

import com.coachera.backend.dto.PaymentRequestDTO;
import com.coachera.backend.dto.PaymentResponseDTO;
import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Payment;
import com.coachera.backend.entity.Student;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.CourseRepository;
import com.coachera.backend.repository.PaymentRepository;
import com.coachera.backend.repository.StudentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    
    @Transactional
    public PaymentResponseDTO createPaymentIntent(PaymentRequestDTO request, Integer studentId) {
        try {
            // Validate student and course
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
            
            Course course = courseRepository.findById(request.getCourseId().intValue())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
            
            // Convert amount to cents (Stripe expects amounts in smallest currency unit)
            long amountInCents = request.getAmount().multiply(BigDecimal.valueOf(100)).longValue();
            
            // Create payment intent with Stripe
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(request.getCurrency().toLowerCase())
                    .setDescription(request.getDescription() != null ? request.getDescription() : 
                            "Payment for course: " + course.getTitle())
                    .putMetadata("course_id", course.getId().toString())
                    .putMetadata("student_id", student.getId().toString())
                    .build();
            
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            
            // Save payment record to database
            Payment payment = new Payment();
            payment.setStripePaymentIntentId(paymentIntent.getId());
            payment.setAmount(request.getAmount());
            payment.setCurrency(request.getCurrency());
            payment.setStatus(Payment.PaymentStatus.PENDING);
            payment.setStudent(student);
            payment.setCourse(course);
            payment.setDescription(request.getDescription());
            
            Payment savedPayment = paymentRepository.save(payment);
            
            log.info("Created payment intent: {} for student: {} and course: {}", 
                    paymentIntent.getId(), studentId, request.getCourseId());
            
            return PaymentResponseDTO.fromPayment(savedPayment, paymentIntent.getClientSecret());
            
        } catch (StripeException e) {
            log.error("Error creating payment intent: {}", e.getMessage());
            throw new RuntimeException("Failed to create payment intent", e);
        }
    }
    
    @Transactional
    public PaymentResponseDTO confirmPayment(String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            
            Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
            
            // Update payment status based on Stripe status
            switch (paymentIntent.getStatus()) {
                case "succeeded":
                    payment.setStatus(Payment.PaymentStatus.SUCCEEDED);
                    break;
                case "canceled":
                    payment.setStatus(Payment.PaymentStatus.CANCELLED);
                    break;
                case "requires_payment_method":
                case "requires_confirmation":
                case "requires_action":
                    payment.setStatus(Payment.PaymentStatus.PENDING);
                    break;
                default:
                    payment.setStatus(Payment.PaymentStatus.FAILED);
            }
            
            Payment updatedPayment = paymentRepository.save(payment);
            
            log.info("Updated payment status: {} for payment intent: {}", 
                    updatedPayment.getStatus(), paymentIntentId);
            
            return PaymentResponseDTO.fromPayment(updatedPayment, paymentIntent.getClientSecret());
            
        } catch (StripeException e) {
            log.error("Error confirming payment: {}", e.getMessage());
            throw new RuntimeException("Failed to confirm payment", e);
        }
    }
    
    public List<PaymentResponseDTO> getStudentPayments(Integer studentId) {
        List<Payment> payments = paymentRepository.findByStudentId(studentId);
        return payments.stream()
                .map(payment -> PaymentResponseDTO.fromPayment(payment, null))
                .collect(Collectors.toList());
    }
    
    public PaymentResponseDTO getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        return PaymentResponseDTO.fromPayment(payment, null);
    }
    
    public PaymentResponseDTO getPaymentByStripeId(String stripePaymentIntentId) {
        Payment payment = paymentRepository.findByStripePaymentIntentId(stripePaymentIntentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        return PaymentResponseDTO.fromPayment(payment, null);
    }
} 