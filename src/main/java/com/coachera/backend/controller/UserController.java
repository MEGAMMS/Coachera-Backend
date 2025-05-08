package com.coachera.backend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.coachera.backend.entity.User;

@RestController
@RequestMapping("/api/users")
public class UserController {

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
}
