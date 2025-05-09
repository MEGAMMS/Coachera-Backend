package com.coachera.backend.security;

import com.coachera.backend.dto.AuthResponse;
import com.coachera.backend.dto.LoginRequest;
import com.coachera.backend.dto.RegisterRequest;
import com.coachera.backend.dto.UserDTO;
import com.coachera.backend.entity.User;
import com.coachera.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import for @Transactional

@Service
@RequiredArgsConstructor
@Transactional // Ensures the operation is atomic
public class AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final AuthenticationManager authenticationManager;
        private final TokenService tokenService;
        // CustomUserDetailsService is not directly needed here for login if
        // AuthenticationManager is used correctly.
        // It is used by DaoAuthenticationProvider which is configured in
        // SecurityConfig.

        /**
         * Registers a new user.
         * 
         * @param registerRequest DTO containing registration details.
         * @return The created User entity.
         * @throws IllegalArgumentException if username or email is already taken.
         */
        public User register(RegisterRequest registerRequest) {
                if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
                        throw new IllegalArgumentException("Username is already taken!");
                }
                if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
                        throw new IllegalArgumentException("Email is already in use!");
                }

                User user = User.builder()
                                .username(registerRequest.getUsername())
                                .email(registerRequest.getEmail())
                                .password(passwordEncoder.encode(registerRequest.getPassword()))
                                .isVerified(false) // Default to false, can be changed based on verification flow
                                .build();
                return userRepository.save(user);
        }

        /**
         * Authenticates a user and generates an access token.
         * 
         * @param loginRequest DTO containing login credentials.
         * @return AuthResponse containing the access token and username.
         * @throws BadCredentialsException if authentication fails.
         */
        public AuthResponse login(LoginRequest loginRequest) {
                // The AuthenticationManager will use the configured AuthenticationProvider
                // (DaoAuthenticationProvider),
                // which in turn uses CustomUserDetailsService to load the user and
                // PasswordEncoder to verify the password.
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                loginRequest.getIdentifier(), // This is passed to
                                                                              // CustomUserDetailsService's
                                                                              // loadUserByUsername
                                                loginRequest.getPassword()));

                // If authentication is successful, the principal is UserDetails.
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                User user = userRepository.findByUsername(userDetails.getUsername()).get();
                String username = user.getUsername(); // This is the actual username stored in the User entity

                // Generate our custom simple token
                String token = tokenService.generateToken(username);
                UserDTO userDTO = new UserDTO(user);
                return new AuthResponse(token, userDTO);
        }

        /**
         * Invalidates the user's current access token.
         * 
         * @param authorizationHeader The Authorization header containing the Bearer
         *                            token.
         * @return true if logout was successful, false otherwise.
         */
        public boolean logout(String authorizationHeader) {
                if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                        String token = authorizationHeader.substring(7);
                        if (tokenService.validateToken(token)) { // Optional: check if token is valid before
                                                                 // invalidating
                                tokenService.invalidateToken(token);
                                return true;
                        }
                }
                return false;
        }
}
