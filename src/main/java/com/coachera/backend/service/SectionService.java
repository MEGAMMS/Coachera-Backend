package com.coachera.backend.service;

import com.coachera.backend.dto.SectionDTO;
import com.coachera.backend.entity.Section;
import com.coachera.backend.entity.Module;
import com.coachera.backend.exception.DuplicateOrderIndexException;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.SectionRepository;
import com.coachera.backend.repository.ModuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SectionService {

    private final SectionRepository sectionRepository;
    private final ModuleRepository moduleRepository;

    public SectionService(SectionRepository sectionRepository, ModuleRepository moduleRepository) {
        this.sectionRepository = sectionRepository;
        this.moduleRepository = moduleRepository;
    }

    
    public SectionDTO createSection(Integer moduleId, SectionDTO sectionDTO) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + moduleId));

        validateSectionOrderIndexUniqueness(moduleId, sectionDTO.getOrderIndex(), null);

        Section section = new Section();
        section.setModule(module);
        section.setTitle(sectionDTO.getTitle());
        section.setOrderIndex(sectionDTO.getOrderIndex());

        Section savedSection = sectionRepository.save(section);
        return new SectionDTO(savedSection);
    }

    public SectionDTO updateSection(Integer sectionId, SectionDTO sectionDTO) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));

        validateSectionOrderIndexUniqueness(section.getModule().getId(), sectionDTO.getOrderIndex(), sectionId);

        section.setTitle(sectionDTO.getTitle());
        section.setOrderIndex(sectionDTO.getOrderIndex());

        Section updatedSection = sectionRepository.save(section);
        return new SectionDTO(updatedSection);
    }

    @Transactional(readOnly = true)
    public SectionDTO getSectionById(Integer sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));
        return new SectionDTO(section);
    }

    @Transactional(readOnly = true)
    public List<SectionDTO> getAllSectionsByModuleId(Integer moduleId) {
        if (!moduleRepository.existsById(moduleId)) {
            throw new ResourceNotFoundException("Module not found with id: " + moduleId);
        }

        return sectionRepository.findByModuleIdOrderByOrderIndexAsc(moduleId).stream()
                .map(SectionDTO::new)
                .collect(Collectors.toList());
    }

    public void deleteSection(Integer sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));
        sectionRepository.delete(section);
    }

    private void validateSectionOrderIndexUniqueness(Integer moduleId, Integer orderIndex, Integer excludeSectionId) {
        boolean orderIndexExists = excludeSectionId == null
                ? sectionRepository.existsByModuleIdAndOrderIndex(moduleId, orderIndex)
                : sectionRepository.existsByModuleIdAndOrderIndexAndIdNot(moduleId, orderIndex, excludeSectionId);

        if (orderIndexExists) {
            throw new DuplicateOrderIndexException(
                    "Order index " + orderIndex + " already exists in this module");
        }
    }
}