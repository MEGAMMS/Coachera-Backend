package com.coachera.backend.security;

import com.coachera.backend.dto.AuthResponse;
// import com.coachera.backend.dto.InstructorDTO;
import com.coachera.backend.dto.InstructorRequestDTO;
import com.coachera.backend.dto.LoginRequest;
// import com.coachera.backend.dto.OrganizationDTO;
import com.coachera.backend.dto.OrganizationRequestDTO;
import com.coachera.backend.dto.RegisterRequest;
import com.coachera.backend.dto.RoleDTO;
// import com.coachera.backend.dto.StudentDTO;
import com.coachera.backend.dto.StudentRequestDTO;
import com.coachera.backend.dto.UserDTO;
import com.coachera.backend.entity.Image;
import com.coachera.backend.entity.User;
import com.coachera.backend.entity.enums.RoleType;
import com.coachera.backend.repository.UserRepository;
import com.coachera.backend.service.ImageService;
import com.coachera.backend.service.InstructorService;
import com.coachera.backend.service.OrganizationService;
import com.coachera.backend.service.StudentService;

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
        private final ImageService imageService;

        private final StudentService studentService;
        private final OrganizationService orgService;
        private final InstructorService instructorService;

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
                if (registerRequest.getRole().equals(RoleType.ADMIN)) {
                        throw new IllegalArgumentException("Admin role cannot be self-assigned");
                }
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
                                .role(registerRequest.getRole())
                                .isVerified(false) // Default to false, can be changed based on verification flow
                                .build();
                if (registerRequest.getProfileImageUrl() != null) {
                        Image image = imageService.getImageFromUrl(registerRequest.getProfileImageUrl());
                        user.setProfileImage(image);
                }

                User savedUser = userRepository.save(user);

                // Handle role-specific profile creation with proper type checking
                RoleDTO details = registerRequest.getDetails();
                if (details != null) {
                        switch (registerRequest.getRole()) {
                        case STUDENT:
                                if (!(details instanceof StudentRequestDTO)) {
                                throw new IllegalArgumentException("Invalid details type for STUDENT role. Expected StudentDTO.");
                                }
                                studentService.createStudent((StudentRequestDTO) details, savedUser);
                                break;
                                
                        case INSTRUCTOR:
                                if (!(details instanceof InstructorRequestDTO)) {
                                throw new IllegalArgumentException("Invalid details type for INSTRUCTOR role. Expected InstructorDTO.");
                                }
                                instructorService.createInstructor((InstructorRequestDTO) details, savedUser);
                                break;
                        case ORGANIZATION:
                                if(!(details instanceof OrganizationRequestDTO)){
                                        throw new IllegalArgumentException("Invalid details type for ORGNIZATION role. Expected OrganizationDTO.");
                                }
                                orgService.createOrganization((OrganizationRequestDTO)details,savedUser);
                                break;
                        default:
                                // For roles that don't require additional details
                                break;
                        }
                } else{
                        throw new IllegalArgumentException("Student registration requires additional details");
                }
                
                return savedUser;
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
                        
                       try {
                                // Try to invalidate regardless of expiration
                                tokenService.invalidateToken(token);
                                return true;

                        } catch (Exception e) {
                                // Could not parse token (e.g., malformed, not JWT)
                                return false;
                        }
                }
                return false;
        }
}
