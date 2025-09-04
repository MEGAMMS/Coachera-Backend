package com.coachera.backend.service;

import com.coachera.backend.dto.MaterialDTO;
import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Instructor;
import com.coachera.backend.entity.Material;
import com.coachera.backend.entity.Section;
import com.coachera.backend.entity.User;
import com.coachera.backend.entity.Video;
import com.coachera.backend.exception.DuplicateOrderIndexException;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.InstructorRepository;
import com.coachera.backend.repository.MaterialRepository;
// import com.coachera.backend.repository.OrganizationRepository;
import com.coachera.backend.repository.SectionRepository;
import com.coachera.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final SectionRepository sectionRepository;
    private final UserRepository userRepository;
    private final InstructorRepository instructorRepository;
    // private final OrganizationRepository organizationRepository;

    private final VideoService videoService;

    public MaterialDTO createMaterial(Integer sectionId, MaterialDTO materialDTO, User user) {

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));

        if (!isInstructorOfCourse(user, section.getModule().getCourse())) {
            throw new AccessDeniedException("You are not allowed to delete this course");
        }
        validateMaterialOrderIndexUniqueness(sectionId, materialDTO.getOrderIndex(), null);

        Material material = new Material();
        material.setSection(section);
        material.setTitle(materialDTO.getTitle());
        material.setOrderIndex(materialDTO.getOrderIndex());
        material.setType(materialDTO.getType());
        Video video = videoService.getVideoFromUrl(materialDTO.getVideoUrl());
        material.setVideo(video);
        material.setArticle(materialDTO.getArticle());

        Material savedMaterial = materialRepository.save(material);
        return new MaterialDTO(savedMaterial);
    }

    public MaterialDTO updateMaterial(Integer materialId, MaterialDTO materialDTO, User user) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found with id: " + materialId));

        // Verify instructor permissions
        if (!isInstructorOfCourse(user, material.getSection().getModule().getCourse())) {
            throw new AuthorizationDeniedException("You don't have permission to modify this material");
        }

        validateMaterialOrderIndexUniqueness(material.getSection().getId(), materialDTO.getOrderIndex(), materialId);

        material.setTitle(materialDTO.getTitle());
        material.setOrderIndex(materialDTO.getOrderIndex());

        Material updatedMaterial = materialRepository.save(material);
        return new MaterialDTO(updatedMaterial);
    }

    @Transactional(readOnly = true)
    public MaterialDTO getMaterialById(Integer materialId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found with id: " + materialId));
        return new MaterialDTO(material);
    }

    @Transactional(readOnly = true)
    public List<MaterialDTO> getAllMaterialsBySectionId(Integer sectionId) {
        if (!sectionRepository.existsById(sectionId)) {
            throw new ResourceNotFoundException("Section not found with id: " + sectionId);
        }

        return materialRepository.findBySectionIdOrderByOrderIndexAsc(sectionId).stream()
                .map(MaterialDTO::new)
                .collect(Collectors.toList());
    }

    public void deleteMaterial(Integer materialId, User user) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found with id: " + materialId));

        // Verify organization ownership
        if (!isInstructorOfCourse(user,material.getSection().getModule().getCourse())) {
            throw new AuthorizationDeniedException("You don't have permission to modify this material");
        }
        materialRepository.delete(material);
    }

    // Helper method
    private void validateMaterialOrderIndexUniqueness(Integer sectionId, Integer orderIndex,
            Integer excludeMaterialId) {
        boolean orderIndexExists = excludeMaterialId == null
                ? materialRepository.existsBySectionIdAndOrderIndex(sectionId, orderIndex)
                : materialRepository.existsBySectionIdAndOrderIndexAndIdNot(sectionId, orderIndex, excludeMaterialId);

        if (orderIndexExists) {
            throw new DuplicateOrderIndexException(
                    "Order index " + orderIndex + " already exists in this section");
        }
    }

    // Helper method
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
}