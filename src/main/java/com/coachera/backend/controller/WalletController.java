package com.coachera.backend.controller;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.CourseWithModulesDTO;
import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.User;
import com.coachera.backend.entity.Wallet;
import com.coachera.backend.service.CourseService;
import com.coachera.backend.service.WalletService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final CourseService courseService;

    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<BigDecimal>> getBalance(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        BigDecimal balance = walletService.getBalance(user);
        return ResponseEntity.ok(ApiResponse.success("Wallet balance retrieved successfully", balance));
    }

    @PostMapping("/add-money")
    public ResponseEntity<ApiResponse<Wallet>> addMoney(
            @RequestParam BigDecimal amount,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Amount must be greater than zero"));
        }
        
        Wallet wallet = walletService.addMoney(user, amount);
        return ResponseEntity.ok(ApiResponse.success("Money added to wallet successfully", wallet));
    }

    @PostMapping("/pay-course/{courseId}")
    public ResponseEntity<ApiResponse<String>> payForCourse(
            @PathVariable Integer courseId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        CourseWithModulesDTO courseDTO = courseService.getCourseById(courseId);
        if (courseDTO == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Course not found"));
        }
        
        // Convert DTO to Entity for payment processing
        Course course = new Course();
        course.setId(courseDTO.getId());
        course.setPrice(courseDTO.getPrice());
        
        boolean paymentSuccessful = walletService.payForCourse(user, course);
        
        if (paymentSuccessful) {
            return ResponseEntity.ok(ApiResponse.success("Course payment completed", "Payment successful"));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Insufficient wallet balance"));
        }
    }

    @GetMapping("/check-balance/{courseId}")
    public ResponseEntity<ApiResponse<Boolean>> checkSufficientBalance(
            @PathVariable Integer courseId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        CourseWithModulesDTO courseDTO = courseService.getCourseById(courseId);
        if (courseDTO == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Course not found"));
        }
        
        boolean hasBalance = walletService.hasSufficientBalance(user, courseDTO.getPrice());
        return ResponseEntity.ok(ApiResponse.success("Balance check completed", hasBalance));
    }
}
