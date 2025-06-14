package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.OrganizationDTO;
import com.coachera.backend.dto.pagination.PaginationRequest;
import com.coachera.backend.entity.User;
import com.coachera.backend.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
        public ApiResponse<?> getAllOrganizations(@Valid PaginationRequest paginationRequest) {

                return ApiResponse.paginated(organizationService.getOrganizations(paginationRequest.toPageable()));

        }

        @GetMapping("/{id}")
        public ApiResponse<?> getOrganization(@PathVariable Integer id) {

                OrganizationDTO org = organizationService.getOrganizationById(id);
                return ApiResponse.success(org);

        }

        @GetMapping("/user")
        public ApiResponse<?> getOrganizationsByUser(
                        @AuthenticationPrincipal User user) {

                OrganizationDTO organization = organizationService.getOrganizationsByUser(user);
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
                return ApiResponse.noContentResponse();

        }
}
