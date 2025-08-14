package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.SectionDTO;
import com.coachera.backend.service.SectionService;

import jakarta.validation.Valid;

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
    public ApiResponse<SectionDTO> createSection(
            @PathVariable Integer moduleId,
            @Valid @RequestBody SectionDTO sectionDTO) {
        SectionDTO createdSection = sectionService.createSection(moduleId, sectionDTO);
        return ApiResponse.created("Section was created successfuly",createdSection);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PutMapping("/{sectionId}")
    public ApiResponse<SectionDTO> updateSection(
            @PathVariable Integer sectionId,
            @Valid @RequestBody SectionDTO sectionDTO) {
        SectionDTO updatedSection = sectionService.updateSection(sectionId, sectionDTO);
        return ApiResponse.success("Section was updated successfuly",updatedSection);
    }

    @GetMapping("/{sectionId}")
    public ApiResponse<SectionDTO> getSectionById(
            @PathVariable Integer moduleId,
            @PathVariable Integer sectionId) {
        SectionDTO sectionDTO = sectionService.getSectionById(sectionId);
        return ApiResponse.success(sectionDTO);
    }

    @GetMapping
    public ApiResponse<List<SectionDTO>> getAllSectionsByModuleId(
            @PathVariable Integer moduleId) {
        List<SectionDTO> sections = sectionService.getAllSectionsByModuleId(moduleId);
        return ApiResponse.success(sections);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @DeleteMapping("/{sectionId}")
    public ApiResponse<Void> deleteSection(
            @PathVariable Integer sectionId) {
        sectionService.deleteSection(sectionId);
        return ApiResponse.noContentResponse();
    }
}