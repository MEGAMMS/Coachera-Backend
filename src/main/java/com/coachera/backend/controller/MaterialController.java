package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.MaterialDTO;
import com.coachera.backend.entity.User;
import com.coachera.backend.service.MaterialService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/materials")
public class MaterialController {

    private final MaterialService materialService;

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping
    public ApiResponse<?> createMaterial(
            @Valid @RequestBody MaterialDTO materialDTO,
            @AuthenticationPrincipal User user) {
        MaterialDTO createdMaterial = materialService.createMaterial(materialDTO, user);
        return ApiResponse.created("Material was created successfully", createdMaterial);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PutMapping("/{materialId}")
    public ApiResponse<?> updateMaterial(
            @PathVariable Integer materialId,
            @Valid @RequestBody MaterialDTO materialDTO,
            @AuthenticationPrincipal User user) {
        MaterialDTO updatedMaterial = materialService.updateMaterial(materialId, materialDTO, user);
        return ApiResponse.success("Material was updated successfully", updatedMaterial);
    }

    @GetMapping("/{materialId}")
    public ApiResponse<?> getMaterialById(
            @PathVariable Integer materialId) {
        MaterialDTO materialDTO = materialService.getMaterialById(materialId);
        return ApiResponse.success(materialDTO);
    }

    @GetMapping("/sections/{sectionId}")
    public ApiResponse<?> getAllMaterialsBySectionId(
            @PathVariable Integer sectionId) {
        List<MaterialDTO> materials = materialService.getAllMaterialsBySectionId(sectionId);
        return ApiResponse.success(materials);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @DeleteMapping("/{materialId}")
    public ApiResponse<?> deleteMaterial(
            @PathVariable Integer materialId,
            @AuthenticationPrincipal User user) {
        materialService.deleteMaterial(materialId, user);
        return ApiResponse.noContentResponse();
    }
}