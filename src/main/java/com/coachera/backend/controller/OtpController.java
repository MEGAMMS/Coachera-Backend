package com.coachera.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.service.EmailService;
import com.coachera.backend.service.OtpService;

@RestController
@RequestMapping("/api/auth")
public class OtpController {

    private final OtpService otpService;
    private final EmailService emailService;

    @Value("${spring.mail.password}")
    private String password_for_the_smpt_server;

    public OtpController(OtpService otpService, EmailService emailService) {
        this.otpService = otpService;
        this.emailService = emailService;
    }

    @PostMapping("/send-otp")
    public ApiResponse<?> sendOtp(@RequestParam String email) {

        String otp = otpService.generateOtp(email);
        emailService.sendOtpEmail(email, otp);
        return ApiResponse.success("OTP sent successfully", null);
    }

    @PostMapping("/validate-otp")
    public ApiResponse<?> validateOtp(@RequestParam String email,
            @RequestParam String otp) throws BadRequestException {
        boolean isValid = otpService.validateOtp(email, otp);
        if (!isValid) {
            throw new BadRequestException("Invalid OTP");
        }
        return ApiResponse.success("OTP is valid", null);
    }
}
