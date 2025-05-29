package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.SkillDTO;
import com.coachera.backend.service.SkillService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<SkillDTO> createSkill(@RequestBody @Valid SkillDTO skillDTO) {
        SkillDTO createdSkill = skillService.createSkill(skillDTO);
        return ApiResponse.created("Skill created successfully", createdSkill);
    }

    @GetMapping("/{id}")
    public ApiResponse<SkillDTO> getSkillById(@PathVariable Integer id) {
        SkillDTO skill = skillService.getSkillById(id);
        return ApiResponse.success(skill);
    }

    @GetMapping
    public ApiResponse<List<SkillDTO>> getAllSkills() {
        List<SkillDTO> skills = skillService.getAllSkills();
        return ApiResponse.success(skills);
    }

    @GetMapping("/search")
    public ApiResponse<List<SkillDTO>> searchSkillsByName(@RequestParam String name) {
        List<SkillDTO> skills = skillService.searchSkillsByName(name);
        return ApiResponse.success(skills);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<SkillDTO> updateSkill(
            @PathVariable Integer id,
            @RequestBody @Valid SkillDTO skillDTO) {
        SkillDTO updatedSkill = skillService.updateSkill(id, skillDTO);
        return ApiResponse.success("Skill updated successfully", updatedSkill);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteSkill(@PathVariable Integer id) {
        skillService.deleteSkill(id);
        return ApiResponse.noContentResponse();
    }
}