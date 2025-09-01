package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.SectionDTO;
import com.coachera.backend.entity.User;
import com.coachera.backend.service.SectionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sections")
public class SectionController {

    private final SectionService sectionService;

    @PreAuthorize("hasRole('ORGANIZATION') or hasRole('INSTRUCTOR')")
    @PostMapping
    public ApiResponse<SectionDTO> createSection(
            @PathVariable Integer moduleId,
            @Valid @RequestBody SectionDTO sectionDTO,
            @AuthenticationPrincipal User user) {
        SectionDTO createdSection = sectionService.createSection(moduleId, sectionDTO,user);
        return ApiResponse.created("Section was created successfuly",createdSection);
    }

    @PreAuthorize("hasRole('ORGANIZATION') or hasRole('INSTRUCTOR')")
    @PutMapping("/{sectionId}")
    public ApiResponse<SectionDTO> updateSection(
            @PathVariable Integer sectionId,
            @Valid @RequestBody SectionDTO sectionDTO,
            @AuthenticationPrincipal User user) {
        SectionDTO updatedSection = sectionService.updateSection(sectionId, sectionDTO,user);
        return ApiResponse.success("Section was updated successfuly",updatedSection);
    }

    @GetMapping("/{sectionId}")
    public ApiResponse<SectionDTO> getSectionById(
            @PathVariable Integer moduleId,
            @PathVariable Integer sectionId) {
        SectionDTO sectionDTO = sectionService.getSectionById(sectionId);
        return ApiResponse.success(sectionDTO);
    }

    @GetMapping("/modules/{moduleId}")
    public ApiResponse<List<SectionDTO>> getAllSectionsByModuleId(
            @PathVariable Integer moduleId) {
        List<SectionDTO> sections = sectionService.getAllSectionsByModuleId(moduleId);
        return ApiResponse.success(sections);
    }

    @PreAuthorize("hasRole('ORGANIZATION') or hasRole('INSTRUCTOR')")
    @DeleteMapping("/{sectionId}")
    public ApiResponse<Void> deleteSection(
            @PathVariable Integer sectionId,
            @AuthenticationPrincipal User user) {
        sectionService.deleteSection(sectionId, user);
        return ApiResponse.noContentResponse();
    }
}