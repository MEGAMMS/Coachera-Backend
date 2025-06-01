package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.MaterialDTO;
import com.coachera.backend.entity.User;
import com.coachera.backend.service.MaterialService;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sections/{sectionId}/materials")
public class MaterialController {

    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PostMapping
    public ApiResponse<?> createMaterial(
            @PathVariable Integer sectionId,
            @Valid @RequestBody MaterialDTO materialDTO,
            @AuthenticationPrincipal User user) {
        MaterialDTO createdMaterial = materialService.createMaterial(sectionId, materialDTO);
        return ApiResponse.created("Material was created successfully", createdMaterial);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PutMapping("/{materialId}")
    public ApiResponse<?> updateMaterial(
            @PathVariable Integer materialId,
            @Valid @RequestBody MaterialDTO materialDTO) {
        MaterialDTO updatedMaterial = materialService.updateMaterial(materialId, materialDTO);
        return ApiResponse.success("Material was updated successfully", updatedMaterial);
    }

    @GetMapping("/{materialId}")
    public ApiResponse<?> getMaterialById(
            @PathVariable Integer materialId) {
        MaterialDTO materialDTO = materialService.getMaterialById(materialId);
        return ApiResponse.success(materialDTO);
    }

    @GetMapping
    public ApiResponse<?> getAllMaterialsBySectionId(
            @PathVariable Integer sectionId) {
        List<MaterialDTO> materials = materialService.getAllMaterialsBySectionId(sectionId);
        return ApiResponse.success(materials);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @DeleteMapping("/{materialId}")
    public ApiResponse<?> deleteMaterial(
            @PathVariable Integer materialId) {
        materialService.deleteMaterial(materialId);
        return ApiResponse.noContentResponse();
    }
}