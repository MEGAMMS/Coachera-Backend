package com.coachera.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coachera.backend.dto.CourseDTO;
import com.coachera.backend.entity.Course;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.CourseRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseService {
    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CourseDTO getCourseById(Integer id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        return convertToDto(course);
    }

    public CourseDTO createCourse(CourseDTO courseDTO) {
        Course course = new Course();
        course.setTitle(courseDTO.getTitle());
        course.setDescription(courseDTO.getDescription());
        // course.setOrg(org);
        Course savedCourse = courseRepository.save(course);
        return convertToDto(savedCourse);
    }

    public CourseDTO updateCourse(Integer id, CourseDTO courseDTO) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        course.setTitle(courseDTO.getTitle());
        course.setDescription(courseDTO.getDescription());
        Course updatedCourse = courseRepository.save(course);
        return convertToDto(updatedCourse);
    }

    public void deleteCourse(Integer id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        courseRepository.delete(course);
    }

    private CourseDTO convertToDto(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        return dto;
    }
}
