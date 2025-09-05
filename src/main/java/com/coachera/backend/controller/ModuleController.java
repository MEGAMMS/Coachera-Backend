
package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.ModuleDTO;
import com.coachera.backend.dto.ModuleRequestDTO;
import com.coachera.backend.entity.User;
import com.coachera.backend.service.ModuleService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/modules")
public class ModuleController {

    private final ModuleService moduleService;

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/courses/{courseId}")
    public ApiResponse<ModuleDTO> createModule(
            @PathVariable Integer courseId,
            @Valid @RequestBody ModuleRequestDTO moduleDTO) {
        ModuleDTO createdModule = moduleService.createModule(courseId, moduleDTO);
        return ApiResponse.success("Module was created successfuly", createdModule);
    }

    @PreAuthorize("hasRole('ORGANIZATION') or hasRole('INSTRUCTOR')")
    @PutMapping("/{moduleId}")
    public ApiResponse<ModuleDTO> updateModule(
            @PathVariable Integer moduleId,
            @Valid @RequestBody ModuleRequestDTO moduleDTO,
            @AuthenticationPrincipal User user) {
        ModuleDTO updatedModule = moduleService.updateModule(moduleId, moduleDTO,user);
        return ApiResponse.success("Module was updated successfuly", updatedModule);
    }

    @GetMapping("/{moduleId}")
    public ApiResponse<ModuleDTO> getModuleById(@PathVariable Integer moduleId) {
        ModuleDTO moduleDTO = moduleService.getModuleById(moduleId);
        return ApiResponse.success(moduleDTO);
    }

    @GetMapping("/courses/{courseId}")
    public ApiResponse<List<ModuleDTO>> getAllModulesByCourseId(@PathVariable Integer courseId) {
        List<ModuleDTO> modules = moduleService.getAllModulesByCourseId(courseId);
        return ApiResponse.success(modules);
    }

    @PreAuthorize("hasRole('ORGANIZATION') or hasRole('INSTRUCTOR')")
    @DeleteMapping("/{moduleId}")
    public ApiResponse<Void> deleteModule(
            @PathVariable Integer moduleId,
            @AuthenticationPrincipal User user) {
        moduleService.deleteModule(moduleId, user);
        return ApiResponse.noContentResponse();
    }
}