package com.coachera.backend.security;

import com.coachera.backend.entity.AccessToken;
import com.coachera.backend.entity.User;
import com.coachera.backend.repository.AccessTokenRepository;
import com.coachera.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {
        private final AccessTokenRepository tokenRepo;
        private final UserRepository userRepo;

        /**
         * Generate a new token for given username.
         */
        public String generateToken(String username) {
                User user = userRepo.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

                // Remove existing tokens
                tokenRepo.deleteByUserId(user.getId());

                String token = UUID.randomUUID().toString();
                AccessToken at = AccessToken.builder()
                                .token(token)
                                .user(user)
                                .expiresAt(LocalDateTime.now().plusHours(8))
                                .build();
                tokenRepo.save(at);
                return token;
        }

        /**
         * Check if token exists and is not expired.
         */
        public boolean validateToken(String token) {
                return tokenRepo.findByToken(token)
                                .map(at -> at.getExpiresAt().isAfter(LocalDateTime.now()))
                                .orElse(false);
        }

        /**
         * Invalidate (delete) a token.
         */
        public void invalidateToken(String token) {
                tokenRepo.findByToken(token).ifPresent(at -> tokenRepo.delete(at));
        }
}
