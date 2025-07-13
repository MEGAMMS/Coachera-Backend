package com.coachera.backend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

// import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.entity.User;
// import com.coachera.backend.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    // private final UserService userService;

    // public route
    @GetMapping("/public-info")
    public String publicInfo() {
        return "This is public";
    }

    // protected route: requires valid token
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public String currentUser(@AuthenticationPrincipal User user) {
        return "Hello, " + user.getUsername();
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminArea() {
        return "Admin dashboard";
    }

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public String studentArea() {
        return "Student dashboard";
    }

    /// Added this for debugging purposes
    // @GetMapping("/{id}")
    // public ApiResponse<?> getUser(@PathVariable Integer id) {

    //     User user = userService.getUserById(id);
    //     return ApiResponse.success(user);
    // }
}
