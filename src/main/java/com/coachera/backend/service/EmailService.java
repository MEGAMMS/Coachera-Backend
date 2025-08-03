package com.coachera.backend.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp + "\nThis code is valid for 5 minutes.");

        mailSender.send(message);
    }

     /**
     * Send simple notification email
     */
    public CompletableFuture<Void> sendNotificationEmail(
            String toEmail, 
            String title, 
            String content, 
            String actionUrl) {
        
        return CompletableFuture.runAsync(() -> {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(toEmail);
                message.setSubject(title);
                message.setText(buildEmailContent(content, actionUrl));
                
                mailSender.send(message);
                log.info("Successfully sent notification email to {}", toEmail);
                
            } catch (Exception e) {
                log.error("Error sending notification email to {}", toEmail, e);
                throw new RuntimeException("Failed to send notification email", e);
            }
        });
    }

    private String buildEmailContent(String content, String actionUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append(content);
        
        if (actionUrl != null && !actionUrl.isEmpty()) {
            sb.append("\n\n");
            sb.append("Click here to view: ").append(actionUrl);
        }
        
        sb.append("\n\n");
        sb.append("Best regards,\n");
        sb.append("The Coachera Team");
        
        return sb.toString();
    }
}
