package com.coachera.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coachera.backend.dto.CourseDTO;
import com.coachera.backend.dto.LearningPathDTO;
import com.coachera.backend.dto.LearningPathWithCoursersDTO;
import com.coachera.backend.entity.*;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.LearningPathRepository;
import com.coachera.backend.repository.OrganizationRepository;

import lombok.AllArgsConstructor;

import com.coachera.backend.repository.CourseRepository;
import com.coachera.backend.repository.ImageRepository;

@Service
@Transactional
@AllArgsConstructor
public class LearningPathService {

    private final LearningPathRepository learningPathRepository;
    private final OrganizationRepository organizationRepository;
    private final CourseRepository courseRepository;
    private final ImageRepository imageRepository;
    private final ModelMapper modelMapper;

    public LearningPathDTO createLearningPath(LearningPathDTO learningPathDTO, Organization organization) {
        // Verify the organization exists (though we're passing it directly, we can still validate)
        if (organization == null) {
            throw new ResourceNotFoundException("Organization not provided");
        }

        LearningPath learningPath = new LearningPath();
        learningPath.setOrganization(organization);
        learningPath.setTitle(learningPathDTO.getTitle());
        learningPath.setDescription(learningPathDTO.getDescription());

        if (learningPathDTO.getImageUrl() != null) {
            Image image = imageRepository.findByUrl(learningPathDTO.getImageUrl())
                    .orElseThrow(() -> new ResourceNotFoundException("Image not found with Url: " + learningPathDTO.getImageUrl()));
            learningPath.setImage(image);
        }

        LearningPath savedLearningPath = learningPathRepository.save(learningPath);
        return new LearningPathDTO(savedLearningPath);
    }

    public LearningPathDTO getLearningPathById(Integer id) {
        LearningPath learningPath = learningPathRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LearningPath not found with id: " + id));
        return new LearningPathDTO(learningPath);
    }

    public List<LearningPathDTO> getAllLearningPaths() {
        return learningPathRepository.findAll().stream()
                .map(learningPath -> new LearningPathDTO(learningPath))
                .collect(Collectors.toList());
    }

    public Page<LearningPathDTO> getLearningPaths(Pageable pageable) {
        return learningPathRepository.findAll(pageable)
                .map(LearningPathDTO::new);
    }

    public List<LearningPathDTO> getLearningPathsByOrganization(Integer organizationId) {
        return learningPathRepository.findByOrganizationId(organizationId).stream()
                .map(learningPath -> new LearningPathDTO(learningPath))
                .collect(Collectors.toList());
    }

    public LearningPathDTO updateLearningPath(Integer id, LearningPathDTO learningPathDTO, Organization organization) {
        LearningPath existingLearningPath = learningPathRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LearningPath not found with id: " + id));

        // Verify the requesting organization owns the learning path
        if (!organization.getId().equals(existingLearningPath.getOrganization().getId())) {
            throw new AccessDeniedException("Your organization doesn't own this learning path");
        }

        modelMapper.map(learningPathDTO, existingLearningPath);

        if (learningPathDTO.getOrgId() != null && 
            !learningPathDTO.getOrgId().equals(existingLearningPath.getOrganization().getId())) {
            Organization newOrganization = organizationRepository.findById(learningPathDTO.getOrgId())
                    .orElseThrow(() -> new ResourceNotFoundException("Organization not found with id: " + learningPathDTO.getOrgId()));
            existingLearningPath.setOrganization(newOrganization);
        }

        if (learningPathDTO.getImageUrl() != null && 
            (existingLearningPath.getImage() == null || 
             !learningPathDTO.getImageUrl().equals(existingLearningPath.getImage().getUrl()))) {
            Image image = imageRepository.findByUrl(learningPathDTO.getImageUrl())
                    .orElseThrow(() -> new ResourceNotFoundException("Image not found with Url: " + learningPathDTO.getImageUrl()));
            existingLearningPath.setImage(image);
        }

        LearningPath updatedLearningPath = learningPathRepository.save(existingLearningPath);
        return new LearningPathDTO(updatedLearningPath);
    }

    public void deleteLearningPath(Integer id, Organization organization) {
        LearningPath learningPath = learningPathRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LearningPath not found with id: " + id));

        // Verify the requesting organization owns the learning path
        if (!organization.getId().equals(learningPath.getOrganization().getId())) {
            throw new AccessDeniedException("Your organization doesn't own this learning path");
        }

        learningPathRepository.delete(learningPath);
    }

    public LearningPathDTO addCourseToLearningPath(Integer learningPathId, Integer courseId, Integer orderIndex, Organization organization) {
        LearningPath learningPath = learningPathRepository.findById(learningPathId)
                .orElseThrow(() -> new ResourceNotFoundException("LearningPath not found with id: " + learningPathId));
        
        // Verify the requesting organization owns the learning path
        if (!organization.getId().equals(learningPath.getOrganization().getId())) {
            throw new AccessDeniedException("Your organization doesn't own this learning path");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        learningPath.addCourse(course, orderIndex);
        LearningPath updatedLearningPath = learningPathRepository.save(learningPath);
        return new LearningPathDTO(updatedLearningPath);
    }

    public LearningPathDTO removeCourseFromLearningPath(Integer learningPathId, Integer courseId, Organization organization) {
        LearningPath learningPath = learningPathRepository.findById(learningPathId)
                .orElseThrow(() -> new ResourceNotFoundException("LearningPath not found with id: " + learningPathId));
        
        // Verify the requesting organization owns the learning path
        if (!organization.getId().equals(learningPath.getOrganization().getId())) {
            throw new AccessDeniedException("Your organization doesn't own this learning path");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        learningPath.removeCourse(course);
        LearningPath updatedLearningPath = learningPathRepository.save(learningPath);
        return new LearningPathDTO(updatedLearningPath);
    }

    public List<CourseDTO> getCoursesBylearningPathId(Integer learningPathId) {
        LearningPath learningPath = learningPathRepository.findById(learningPathId)
                .orElseThrow(() -> new ResourceNotFoundException("Learning Path not found with id: " + learningPathId));

        return learningPath.getCourses().stream()
                .map(LearningPathCourse::getCourse)
                .map(course -> new CourseDTO(course))
                .collect(Collectors.toList());
    }

    public LearningPathWithCoursersDTO getLearningPathWithCoursesById(Integer id) {
        LearningPath learningPath = learningPathRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("LearningPath not found with id: " + id));
        return new LearningPathWithCoursersDTO(learningPath);
    }
}