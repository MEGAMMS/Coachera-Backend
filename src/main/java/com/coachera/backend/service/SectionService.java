package com.coachera.backend.service;

import com.coachera.backend.dto.SectionDTO;
import com.coachera.backend.dto.SectionWithMaterialsDTO;
import com.coachera.backend.entity.Section;
import com.coachera.backend.entity.User;
import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Instructor;
import com.coachera.backend.entity.Material;
import com.coachera.backend.entity.Module;
import com.coachera.backend.exception.DuplicateOrderIndexException;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.SectionRepository;
import com.coachera.backend.repository.UserRepository;

import lombok.AllArgsConstructor;

import com.coachera.backend.repository.InstructorRepository;
import com.coachera.backend.repository.MaterialRepository;
import com.coachera.backend.repository.ModuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class SectionService {

    private final SectionRepository sectionRepository;
    private final ModuleRepository moduleRepository;
    private final MaterialRepository materialRepository;

    private final UserRepository userRepository;
    private final InstructorRepository instructorRepository;

    public SectionDTO createSection(SectionDTO sectionDTO, User user) {
        Module module = moduleRepository.findById(sectionDTO.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + sectionDTO.getModuleId()));

        // validateSectionOrderIndexUniqueness(sectionDTO.getModuleId(), sectionDTO.getOrderIndex(), null);

        if (!isInstructorOfCourse(user, module.getCourse())) {
            throw new IllegalArgumentException("Instructor is not assigned to this course");
        }

        Section section = new Section();
        section.setModule(module);
        section.setTitle(sectionDTO.getTitle());
        section.setOrderIndex(sectionDTO.getOrderIndex());

        module.addSection(section);
        moduleRepository.save(module);

        // Section savedSection = sectionRepository.save(section);
        return new SectionDTO(section);
    }

    public SectionDTO updateSection(Integer sectionId, SectionDTO sectionDTO, User user) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));

        validateSectionOrderIndexUniqueness(section.getModule().getId(), sectionDTO.getOrderIndex(), sectionId);

        if (!isInstructorOfCourse(user, section.getModule().getCourse())) {
            throw new IllegalArgumentException("Instructor is not assigned to this course");
        }

        section.setTitle(sectionDTO.getTitle()); 
        section.setOrderIndex(sectionDTO.getOrderIndex());

        if (sectionDTO.getMaterialIds() != null) {
           sectionDTO.getMaterialIds().forEach(materialId -> {
                Material material = materialRepository.findById(materialId)
                        .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with ID: " + sectionId));
                section.addMaterial(material);
            });
        }

        Section updatedSection = sectionRepository.save(section);
        return new SectionDTO(updatedSection);
    }

    @Transactional(readOnly = true)
    public SectionDTO getSectionById(Integer sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));
        return new SectionWithMaterialsDTO(section);
    }

    @Transactional(readOnly = true)
    public List<SectionDTO> getAllSectionsByModuleId(Integer moduleId) {
        if (!moduleRepository.existsById(moduleId)) {
            throw new ResourceNotFoundException("Module not found with id: " + moduleId);
        }

        return sectionRepository.findByModuleIdOrderByOrderIndexAsc(moduleId).stream()
                .map(SectionWithMaterialsDTO::new)
                .collect(Collectors.toList());
    }

    public void deleteSection(Integer sectionId, User user) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));

        if (!isInstructorOfCourse(user, section.getModule().getCourse())) {
            throw new IllegalArgumentException("Instructor is not assigned to this course");
        }

        sectionRepository.delete(section);
    }

    private boolean isInstructorOfCourse(User user, Course course) {
        if (!userRepository.findById(user.getId()).isPresent()) {
            throw new IllegalArgumentException("User not found");
        }
        if (!user.isInstructor()) {
            throw new IllegalArgumentException("Instructor not found");
        }

        Instructor instructor = instructorRepository.findById(user.getInstructor().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Instructor not found with id: " + user.getInstructor().getId()));

        if (course.getInstructors() == null || course.getInstructors().isEmpty()) {
            return false;
        }
        return course.getInstructors().stream()
                .anyMatch(ci -> ci.getInstructor().equals(instructor));
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