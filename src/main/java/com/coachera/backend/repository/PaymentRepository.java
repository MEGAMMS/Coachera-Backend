package com.coachera.backend.repository;

import com.coachera.backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByStudentId(Integer studentId);
    
    List<Payment> findByCourseId(Integer courseId);
    
    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);
    
    List<Payment> findByStatus(Payment.PaymentStatus status);
} 