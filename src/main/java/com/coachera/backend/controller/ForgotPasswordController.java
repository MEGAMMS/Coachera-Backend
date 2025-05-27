package com.coachera.backend.controller;

import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.service.EmailService;
import com.coachera.backend.service.OtpService;
import com.coachera.backend.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class ForgotPasswordController {

    private final UserService userService;
    private final OtpService otpService;
    private final EmailService emailService;

    @PostMapping("/forgot-password")
    public ApiResponse<?> forgotPassword(@RequestParam String email) throws BadRequestException {
        if (!userService.userExists(email)) {
            throw new BadRequestException("Email not registered");
        }

        String otp = otpService.generateOtp(email);
        emailService.sendOtpEmail(email, otp);
        return ApiResponse.success("OTP sent for password reset", null);
    }

    @PostMapping("/reset-password")
    public ApiResponse<?> resetPassword(@RequestParam String email,
            @RequestParam String otp,
            @RequestParam String newPassword) throws BadRequestException {
        if (!otpService.validateOtp(email, otp)) {
            throw new BadRequestException("Invalid OTP");
        }

        userService.updatePassword(email, newPassword);
        return ApiResponse.success("Password updated successfully", null);
    }
}
