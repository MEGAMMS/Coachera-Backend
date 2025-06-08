
package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.ModuleDTO;
import com.coachera.backend.service.ModuleService;

import io.swagger.v3.core.model.ApiDescription;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
public class ModuleController {

    private final ModuleService moduleService;

    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PostMapping("/courses/{courseId}")
    public ApiResponse<ModuleDTO> createModule(@PathVariable Integer courseId, @Valid @RequestBody ModuleDTO moduleDTO) {
        ModuleDTO createdModule = moduleService.createModule(courseId, moduleDTO);
        return ApiResponse.success("Module was created successfuly",createdModule);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PutMapping("/{moduleId}")
    public ApiResponse<ModuleDTO> updateModule(@PathVariable Integer moduleId, @Valid @RequestBody ModuleDTO moduleDTO) {
        ModuleDTO updatedModule = moduleService.updateModule(moduleId, moduleDTO);
        return ApiResponse.success("Module was updated successfuly",updatedModule);
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

    @PreAuthorize("hasRole('ORGANIZATION')")
    @DeleteMapping("/{moduleId}")
    public ApiResponse<Void> deleteModule(@PathVariable Integer moduleId) {
        moduleService.deleteModule(moduleId);
        return ApiResponse.noContentResponse();
    }
}