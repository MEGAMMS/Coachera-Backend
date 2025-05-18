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
        
            OrganizationDTO createdOrg = organizationService.createOrganization(organizationDTO);
            return ApiResponse.created("Organization was created", createdOrg);
       
    }

    @GetMapping
    public ApiResponse<?> getAllOrganizations() {
     
            List<OrganizationDTO> allOrg = organizationService.getAllOrganizations();
            return ApiResponse.success(allOrg);
      
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getOrganization(@PathVariable Integer id) {
       
            OrganizationDTO org = organizationService.getOrganizationById(id);
            return ApiResponse.success(org);
        
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<?> getOrganizationsByUser(
            @PathVariable Integer userId) {
       
            List<OrganizationDTO> organization = organizationService.getOrganizationsByUser(userId);
            return ApiResponse.success(organization);
      
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<?> updateOrganization(
            @PathVariable Integer id,
            @Valid @RequestBody OrganizationDTO organizationDTO) {
        
            OrganizationDTO updateOrganization = organizationService.updateOrganization(id, organizationDTO);
            return ApiResponse.success("Organization was updated", updateOrganization);
      
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteOrganization(@PathVariable Integer id) {
        
            organizationService.deleteOrganization(id);
            return ApiResponse.noContent();
       
    }
}