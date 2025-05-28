package com.coachera.backend.service;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class OtpService {

    // Store OTPs with expiration time (email -> OtpCache)
    private final ConcurrentHashMap<String, OtpCache> otpStorage = new ConcurrentHashMap<>();
    private static final long OTP_VALID_DURATION = 5 * 60 * 1000; // 5 minutes

    public String generateOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStorage.put(email, new OtpCache(otp, System.currentTimeMillis() + OTP_VALID_DURATION));
        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        OtpCache cachedOtp = otpStorage.get(email);
        if (cachedOtp == null)
            return false;

        if (System.currentTimeMillis() > cachedOtp.getExpirationTime()) {
            otpStorage.remove(email);
            return false;
        }

        return cachedOtp.getOtp().equals(otp);
    }

    private static class OtpCache {
        private final String otp;
        private final long expirationTime;

        OtpCache(String otp, long expirationTime) {
            this.otp = otp;
            this.expirationTime = expirationTime;
        }

        // Getters
        public String getOtp() {
            return otp;
        }

        public long getExpirationTime() {
            return expirationTime;
        }
    }
}
