
package com.coachera.backend.controller;

import com.coachera.backend.dto.ModuleDTO;
import com.coachera.backend.service.ModuleService;

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
    public ResponseEntity<ModuleDTO> createModule(@PathVariable Integer courseId, @Valid @RequestBody ModuleDTO moduleDTO) {
        ModuleDTO createdModule = moduleService.createModule(courseId, moduleDTO);
        return new ResponseEntity<>(createdModule, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PutMapping("/{moduleId}")
    public ResponseEntity<ModuleDTO> updateModule(@PathVariable Integer moduleId, @Valid @RequestBody ModuleDTO moduleDTO) {
        ModuleDTO updatedModule = moduleService.updateModule(moduleId, moduleDTO);
        return ResponseEntity.ok(updatedModule);
    }

    @GetMapping("/{moduleId}")
    public ResponseEntity<ModuleDTO> getModuleById(@PathVariable Integer moduleId) {
        ModuleDTO moduleDTO = moduleService.getModuleById(moduleId);
        return ResponseEntity.ok(moduleDTO);
    }


    @GetMapping("/courses/{courseId}")
    public ResponseEntity<List<ModuleDTO>> getAllModulesByCourseId(@PathVariable Integer courseId) {
        List<ModuleDTO> modules = moduleService.getAllModulesByCourseId(courseId);
        return ResponseEntity.ok(modules);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @DeleteMapping("/{moduleId}")
    public ResponseEntity<Void> deleteModule(@PathVariable Integer moduleId) {
        moduleService.deleteModule(moduleId);
        return ResponseEntity.noContent().build();
    }
}