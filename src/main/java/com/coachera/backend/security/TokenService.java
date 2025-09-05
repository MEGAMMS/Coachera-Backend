package com.coachera.backend.security;

import com.coachera.backend.entity.AccessToken;
import com.coachera.backend.entity.User;
import com.coachera.backend.repository.AccessTokenRepository;
import com.coachera.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {
        private final AccessTokenRepository tokenRepo;
        private final UserRepository userRepo;

        /**
         * Generate a new token for given username.
         * Multiple tokens per user are allowed.
         */
        @Transactional
        public String generateToken(String username) {
                User user = userRepo.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

                String token = UUID.randomUUID().toString();
                AccessToken at = AccessToken.builder()
                                .token(token)
                                .user(user)
                                .expiresAt(LocalDateTime.now().plusDays(8))
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
        @Transactional
        public void invalidateToken(String token) {
                tokenRepo.findByToken(token).ifPresent(at -> tokenRepo.delete(at));
        }

        /**
         * Check if user has any valid tokens.
         */
        public boolean hasValidToken(String username) {
                User user = userRepo.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
                
                List<AccessToken> tokens = tokenRepo.findByUserId(user.getId());
                return tokens.stream()
                                .anyMatch(token -> token.getExpiresAt().isAfter(LocalDateTime.now()));
        }

        /**
         * Get all valid tokens for a user.
         */
        public List<String> getValidTokens(String username) {
                User user = userRepo.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
                
                return tokenRepo.findByUserId(user.getId())
                                .stream()
                                .filter(token -> token.getExpiresAt().isAfter(LocalDateTime.now()))
                                .map(AccessToken::getToken)
                                .toList();
        }

        /**
         * Force logout from all devices by invalidating all tokens for a user.
         */
        @Transactional
        public void forceLogoutAllDevices(String username) {
                User user = userRepo.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
                
                tokenRepo.deleteByUserId(user.getId());
        }
}
