package com.coachera.backend.service;

import com.coachera.backend.dto.PaymentRequestDTO;
import com.coachera.backend.dto.PaymentResponseDTO;
import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Payment;
import com.coachera.backend.entity.Student;
import com.coachera.backend.entity.User;
import com.coachera.backend.repository.CourseRepository;
import com.coachera.backend.repository.PaymentRepository;
import com.coachera.backend.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Student testStudent;
    private Course testCourse;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        // Create test user
        User testUser = new User();
        testUser.setId(1);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");

        // Create test student
        testStudent = new Student();
        testStudent.setId(1);
        testStudent.setUser(testUser);
        testStudent.setFirstName("John");
        testStudent.setLastName("Doe");

        // Create test course
        testCourse = new Course();
        testCourse.setId(1);
        testCourse.setTitle("Test Course");
        testCourse.setPrice(new BigDecimal("99.99"));

        // Create test payment
        testPayment = new Payment();
        testPayment.setId(1L);
        testPayment.setStripePaymentIntentId("pi_test_123456789");
        testPayment.setAmount(new BigDecimal("99.99"));
        testPayment.setCurrency("USD");
        testPayment.setStatus(Payment.PaymentStatus.PENDING);
        testPayment.setStudent(testStudent);
        testPayment.setCourse(testCourse);
    }

    @Test
    void testGetStudentPayments() {
        // Given
        when(paymentRepository.findByStudentId(1)).thenReturn(Arrays.asList(testPayment));

        // When
        List<PaymentResponseDTO> result = paymentService.getStudentPayments(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPayment.getId(), result.get(0).getId());
        assertEquals(testPayment.getStripePaymentIntentId(), result.get(0).getStripePaymentIntentId());
        assertEquals(testPayment.getAmount(), result.get(0).getAmount());
        assertEquals(testPayment.getCurrency(), result.get(0).getCurrency());
        assertEquals(testPayment.getStatus(), result.get(0).getStatus());
    }

    @Test
    void testGetPaymentById() {
        // Given
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));

        // When
        PaymentResponseDTO result = paymentService.getPaymentById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testPayment.getId(), result.getId());
        assertEquals(testPayment.getStripePaymentIntentId(), result.getStripePaymentIntentId());
        assertEquals(testPayment.getAmount(), result.getAmount());
        assertEquals(testPayment.getCurrency(), result.getCurrency());
        assertEquals(testPayment.getStatus(), result.getStatus());
    }

    @Test
    void testGetPaymentByStripeId() {
        // Given
        when(paymentRepository.findByStripePaymentIntentId("pi_test_123456789"))
                .thenReturn(Optional.of(testPayment));

        // When
        PaymentResponseDTO result = paymentService.getPaymentByStripeId("pi_test_123456789");

        // Then
        assertNotNull(result);
        assertEquals(testPayment.getId(), result.getId());
        assertEquals(testPayment.getStripePaymentIntentId(), result.getStripePaymentIntentId());
        assertEquals(testPayment.getAmount(), result.getAmount());
        assertEquals(testPayment.getCurrency(), result.getCurrency());
        assertEquals(testPayment.getStatus(), result.getStatus());
    }
} 