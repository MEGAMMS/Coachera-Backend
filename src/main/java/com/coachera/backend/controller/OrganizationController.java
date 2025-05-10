package com.coachera.backend.controller;

import com.coachera.backend.dto.OrganizationDTO;
import com.coachera.backend.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    public ResponseEntity<OrganizationDTO> createOrganization(
            @Valid @RequestBody OrganizationDTO organizationDTO) {
        OrganizationDTO createdOrg = organizationService.createOrganization(organizationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrg);
    }
    
    @GetMapping
    public ResponseEntity<List<OrganizationDTO>> getAllOrganizations()
    {
        return ResponseEntity.ok(organizationService.getAllOrganizations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationDTO> getOrganization(@PathVariable Integer id) {
        return ResponseEntity.ok(organizationService.getOrganizationById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrganizationDTO>> getOrganizationsByUser(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(organizationService.getOrganizationsByUser(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizationDTO> updateOrganization(
            @PathVariable Integer id,
            @Valid @RequestBody OrganizationDTO organizationDTO) {
        return ResponseEntity.ok(organizationService.updateOrganization(id, organizationDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Integer id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }
}