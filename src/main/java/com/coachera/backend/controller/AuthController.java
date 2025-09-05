
package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.AuthResponse;
import com.coachera.backend.dto.LoginRequest;
import com.coachera.backend.dto.RegisterRequest;
import com.coachera.backend.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            authService.register(registerRequest);
            return ApiResponse.success("Registered successfully!", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = authService.login(loginRequest);
            return ApiResponse.success("logged in successfuly",authResponse);
        } catch (BadCredentialsException e) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, "Invalid username/email or password");
        }
    }

    @PostMapping("/logout")
    public ApiResponse<?> logoutUser(@RequestHeader("Authorization") String authorizationHeader) {
        boolean loggedOut = authService.logout(authorizationHeader);
        if (loggedOut) {
            return ApiResponse.success("Logged out successfully.",null);
        }
        return ApiResponse.error(HttpStatus.BAD_REQUEST, "Logout failed. Invalid or missing token.");
    }

    @GetMapping("/token/status")
    public ApiResponse<?> checkTokenStatus(@RequestParam String username) {
        try {
            boolean hasValidToken = authService.hasValidToken(username);
            return ApiResponse.success("Token status retrieved successfully", hasValidToken);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/tokens")
    public ApiResponse<?> getValidTokens(@RequestParam String username) {
        try {
            List<String> validTokens = authService.getValidTokens(username);
            return ApiResponse.success("Valid tokens retrieved successfully", validTokens);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/logout/all")
    public ApiResponse<?> forceLogoutAllDevices(@RequestParam String username) {
        try {
            authService.forceLogoutAllDevices(username);
            return ApiResponse.success("Successfully logged out from all devices.", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
