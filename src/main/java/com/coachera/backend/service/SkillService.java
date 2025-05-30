package com.coachera.backend.service;

import com.coachera.backend.dto.SkillDTO;
import com.coachera.backend.entity.Skill;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.SkillRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SkillService {

    private final SkillRepository skillRepository;
    private final ModelMapper modelMapper;

    public SkillService(SkillRepository skillRepository, ModelMapper modelMapper) {
        this.skillRepository = skillRepository;
        this.modelMapper = modelMapper;
    }

    public SkillDTO createSkill(SkillDTO skillDTO) {
        if (skillRepository.existsByName(skillDTO.getName())) {
            throw new IllegalArgumentException("Skill with this name already exists");
        }

        Skill skill = new Skill();
        skill.setName(skillDTO.getName());

        Skill savedSkill = skillRepository.save(skill);
        return modelMapper.map(savedSkill, SkillDTO.class);
    }

    public SkillDTO getSkillById(Integer id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));
        return modelMapper.map(skill, SkillDTO.class);
    }

    public List<SkillDTO> getAllSkills() {
        return skillRepository.findAll().stream()
                .map(skill -> modelMapper.map(skill, SkillDTO.class))
                .collect(Collectors.toList());
    }

    public SkillDTO updateSkill(Integer id, SkillDTO skillDTO) {
        Skill existingSkill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));

        if (!existingSkill.getName().equals(skillDTO.getName()) && 
            skillRepository.existsByName(skillDTO.getName())) {
            throw new IllegalArgumentException("Skill with this name already exists");
        }

        modelMapper.map(skillDTO, existingSkill);
        Skill updatedSkill = skillRepository.save(existingSkill);
        return modelMapper.map(updatedSkill, SkillDTO.class);
    }

    public void deleteSkill(Integer id) {
        if (!skillRepository.existsById(id)) {
            throw new ResourceNotFoundException("Skill not found with id: " + id);
        }
        skillRepository.deleteById(id);
    }

    public List<SkillDTO> searchSkillsByName(String name) {
        return skillRepository.findByNameContainingIgnoreCase(name).stream()
                .map(skill -> modelMapper.map(skill, SkillDTO.class))
                .collect(Collectors.toList());
    }
}