package com.coachera.backend.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.entity.User;
import com.coachera.backend.service.CertificateService;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/student/certificates")
@PreAuthorize("hasRole('STUDENT')")
@RequiredArgsConstructor
public class StudentCertificateController {
    private final CertificateService certificateService;

    @GetMapping
    public ApiResponse<?> getCertificatesByStudent(
            @AuthenticationPrincipal User user) {
        return ApiResponse.success(certificateService.getCertificatesByStudent(user));
    }

    

}
