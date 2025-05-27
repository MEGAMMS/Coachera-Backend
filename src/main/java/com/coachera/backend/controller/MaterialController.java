package com.coachera.backend.controller;

import com.coachera.backend.dto.MaterialDTO;
import com.coachera.backend.service.MaterialService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<MaterialDTO> createMaterial(
            @PathVariable Integer sectionId,
            @Valid @RequestBody MaterialDTO materialDTO) {
        MaterialDTO createdMaterial = materialService.createMaterial(sectionId, materialDTO);
        return new ResponseEntity<>(createdMaterial, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PutMapping("/{materialId}")
    public ResponseEntity<MaterialDTO> updateMaterial(
            @PathVariable Integer materialId,
            @Valid @RequestBody MaterialDTO materialDTO) {
        MaterialDTO updatedMaterial = materialService.updateMaterial(materialId, materialDTO);
        return ResponseEntity.ok(updatedMaterial);
    }

    @GetMapping("/{materialId}")
    public ResponseEntity<MaterialDTO> getMaterialById(
            @PathVariable Integer materialId) {
        MaterialDTO materialDTO = materialService.getMaterialById(materialId);
        return ResponseEntity.ok(materialDTO);
    }

    @GetMapping
    public ResponseEntity<List<MaterialDTO>> getAllMaterialsBySectionId(
            @PathVariable Integer sectionId) {
        List<MaterialDTO> materials = materialService.getAllMaterialsBySectionId(sectionId);
        return ResponseEntity.ok(materials);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @DeleteMapping("/{materialId}")
    public ResponseEntity<Void> deleteMaterial(
            @PathVariable Integer materialId) {
        materialService.deleteMaterial(materialId);
        return ResponseEntity.noContent().build();
    }
}