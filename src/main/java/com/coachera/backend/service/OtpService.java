package com.coachera.backend.service;

import java.time.LocalDateTime;
import java.util.Random;

import java.util.Optional;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.coachera.backend.entity.OtpVerification;
import com.coachera.backend.repository.OtpRepository;
import com.coachera.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final OtpRepository otpRepository;
    private final JavaMailSender mailSender;
    
    private String fromEmail;
    
    // Generate and send OTP
    public void generateAndSendOtp(String email) {
        // Delete existing OTPs for this email
        otpRepository.deleteByEmail(email);
        

        String code = String.format("%06d", new Random().nextInt(999999));
        
        // Save with 15-minute expiration
        OtpVerification otp = OtpVerification.builder()
            .email(email)
            .code(code)
            .expiryTime(LocalDateTime.now().plusMinutes(15))
            .build();
        otpRepository.save(otp);
        
        // Send email
        sendOtpEmail(email, code);
    }
    
    private void sendOtpEmail(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Your Verification Code");
        message.setText("Your OTP is: " + code + "\nExpires in 15 minutes");
        mailSender.send(message);
    }
    
    // Verify OTP
    public boolean verifyOtp(String email, String code) {
        Optional<OtpVerification> otp = otpRepository.findByEmail(email);
        
        if (otp.isEmpty()) return false;
        if (LocalDateTime.now().isAfter(otp.get().getExpiryTime())) return false;
        if (!otp.get().getCode().equals(code)) return false;
        
        // Delete OTP after successful verification
        otpRepository.deleteByEmail(email);
        return true;
    }
}