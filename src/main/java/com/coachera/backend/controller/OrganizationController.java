package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.OrganizationDTO;
import com.coachera.backend.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<?> createOrganization(
            @Valid @RequestBody OrganizationDTO organizationDTO) {
        try {
            OrganizationDTO createdOrg = organizationService.createOrganization(organizationDTO);
            return ApiResponse.created("Organization was created", createdOrg);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping
    public ApiResponse<?> getAllOrganizations() {
        try {
            List<OrganizationDTO> allOrg = organizationService.getAllOrganizations();
            return ApiResponse.success(allOrg);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getOrganization(@PathVariable Integer id) {
        try {
            OrganizationDTO org = organizationService.getOrganizationById(id);
            return ApiResponse.success(org);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<?> getOrganizationsByUser(
            @PathVariable Integer userId) {
        try {
            List<OrganizationDTO> organization = organizationService.getOrganizationsByUser(userId);
            return ApiResponse.success(organization);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<?> updateOrganization(
            @PathVariable Integer id,
            @Valid @RequestBody OrganizationDTO organizationDTO) {
        try {
            OrganizationDTO updateOrganization = organizationService.updateOrganization(id, organizationDTO);
            return ApiResponse.success("Organization was updated", updateOrganization);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteOrganization(@PathVariable Integer id) {
        try {
            organizationService.deleteOrganization(id);
            return ApiResponse.noContent();
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}