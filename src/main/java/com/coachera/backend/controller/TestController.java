package com.coachera.backend.controller;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final JavaMailSender mailSender;

    @GetMapping("/test-email")
    public String testEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("teto75928@gmail.com");
        message.setSubject("Test Email");
        message.setText("This is a test email.");
        mailSender.send(message);
        return "Email sent!";
    }
}
