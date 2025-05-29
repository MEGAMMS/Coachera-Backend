package com.coachera.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coachera.backend.dto.LearningPathDTO;
import com.coachera.backend.entity.*;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.LearningPathRepository;
import com.coachera.backend.repository.OrganizationRepository;
import com.coachera.backend.repository.CourseRepository;
import com.coachera.backend.repository.ImageRepository;

@Service
@Transactional
public class LearningPathService {

    private final LearningPathRepository learningPathRepository;
    private final OrganizationRepository organizationRepository;
    private final CourseRepository courseRepository;
    private final ImageRepository imageRepository;
    private final ModelMapper modelMapper;

    public LearningPathService(LearningPathRepository learningPathRepository,
                             OrganizationRepository organizationRepository,
                             CourseRepository courseRepository,
                             ImageRepository imageRepository,
                             ModelMapper modelMapper) {
        this.learningPathRepository = learningPathRepository;
        this.organizationRepository = organizationRepository;
        this.courseRepository = courseRepository;
        this.imageRepository = imageRepository;
        this.modelMapper = modelMapper;
    }

    public LearningPathDTO createLearningPath(LearningPathDTO learningPathDTO) {
        Organization organization = organizationRepository.findById(learningPathDTO.getOrgId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with id: " + learningPathDTO.getOrgId()));

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

    public List<LearningPathDTO> getLearningPathsByOrganization(Integer organizationId) {
        return learningPathRepository.findByOrganizationId(organizationId).stream()
                .map(learningPath -> new LearningPathDTO(learningPath))
                .collect(Collectors.toList());
    }

    public LearningPathDTO updateLearningPath(Integer id, LearningPathDTO learningPathDTO) {
        LearningPath existingLearningPath = learningPathRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LearningPath not found with id: " + id));

        modelMapper.map(learningPathDTO, existingLearningPath);

        if (learningPathDTO.getOrgId() != null && 
            !learningPathDTO.getOrgId().equals(existingLearningPath.getOrganization().getId())) {
            Organization organization = organizationRepository.findById(learningPathDTO.getOrgId())
                    .orElseThrow(() -> new ResourceNotFoundException("Organization not found with id: " + learningPathDTO.getOrgId()));
            existingLearningPath.setOrganization(organization);
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

    public void deleteLearningPath(Integer id) {
        if (!learningPathRepository.existsById(id)) {
            throw new ResourceNotFoundException("LearningPath not found with id: " + id);
        }
        learningPathRepository.deleteById(id);
    }

    public LearningPathDTO addCourseToLearningPath(Integer learningPathId, Integer courseId, Integer orderIndex) {
        LearningPath learningPath = learningPathRepository.findById(learningPathId)
                .orElseThrow(() -> new ResourceNotFoundException("LearningPath not found with id: " + learningPathId));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        learningPath.addCourse(course, orderIndex);
        LearningPath updatedLearningPath = learningPathRepository.save(learningPath);
        return new LearningPathDTO(updatedLearningPath);
    }

    public LearningPathDTO removeCourseFromLearningPath(Integer learningPathId, Integer courseId) {
        LearningPath learningPath = learningPathRepository.findById(learningPathId)
                .orElseThrow(() -> new ResourceNotFoundException("LearningPath not found with id: " + learningPathId));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        learningPath.removeCourse(course);
        LearningPath updatedLearningPath = learningPathRepository.save(learningPath);
        return new LearningPathDTO(updatedLearningPath);
    }
}