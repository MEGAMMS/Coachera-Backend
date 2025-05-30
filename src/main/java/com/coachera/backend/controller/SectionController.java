package com.coachera.backend.controller;

import com.coachera.backend.dto.SectionDTO;
import com.coachera.backend.service.SectionService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/modules/{moduleId}/sections")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PostMapping
    public ResponseEntity<SectionDTO> createSection(
            @PathVariable Integer moduleId,
            @Valid @RequestBody SectionDTO sectionDTO) {
        SectionDTO createdSection = sectionService.createSection(moduleId, sectionDTO);
        return new ResponseEntity<>(createdSection, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PutMapping("/{sectionId}")
    public ResponseEntity<SectionDTO> updateSection(
            @PathVariable Integer sectionId,
            @Valid @RequestBody SectionDTO sectionDTO) {
        SectionDTO updatedSection = sectionService.updateSection(sectionId, sectionDTO);
        return ResponseEntity.ok(updatedSection);
    }

    @GetMapping("/{sectionId}")
    public ResponseEntity<SectionDTO> getSectionById(
            @PathVariable Integer moduleId,
            @PathVariable Integer sectionId) {
        SectionDTO sectionDTO = sectionService.getSectionById(sectionId);
        return ResponseEntity.ok(sectionDTO);
    }

    @GetMapping
    public ResponseEntity<List<SectionDTO>> getAllSectionsByModuleId(
            @PathVariable Integer moduleId) {
        List<SectionDTO> sections = sectionService.getAllSectionsByModuleId(moduleId);
        return ResponseEntity.ok(sections);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @DeleteMapping("/{sectionId}")
    public ResponseEntity<Void> deleteSection(
            @PathVariable Integer sectionId) {
        sectionService.deleteSection(sectionId);
        return ResponseEntity.noContent().build();
    }
}