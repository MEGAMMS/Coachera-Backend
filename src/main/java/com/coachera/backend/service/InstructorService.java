package com.coachera.backend.service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coachera.backend.dto.CourseDTO;
import com.coachera.backend.dto.InstructorDTO;
import com.coachera.backend.dto.InstructorRequestDTO;
import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.CourseInstructor;
import com.coachera.backend.entity.Instructor;
import com.coachera.backend.entity.Organization;
import com.coachera.backend.entity.User;
import com.coachera.backend.exception.ConflictException;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.CourseRepository;
import com.coachera.backend.repository.InstructorRepository;
import com.coachera.backend.repository.OrganizationRepository;
import com.coachera.backend.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class InstructorService {

    private final InstructorRepository instructorRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final CourseRepository courseRepository;
    private final OrganizationRepository organizationRepository;

    public InstructorDTO createInstructor(InstructorRequestDTO requestDTO, User user) {
        if (!userRepository.findById(user.getId()).isPresent()) {
            throw new IllegalArgumentException("User must be saved before creating instructor profile");
        }
        if (instructorRepository.existsByUserId(user.getId())) {
            throw new ConflictException("User already has an instructor profile");
        }

        Instructor instructor = new Instructor();
        instructor.setUser(user);
        instructor.setName(requestDTO.getName());
        instructor.setBio(requestDTO.getBio());

        Instructor savedInstructor = instructorRepository.save(instructor);
        return new InstructorDTO(savedInstructor);
    }

    public InstructorDTO getInstructorByUser(User user) {
        Integer instructorId = instructorRepository.findByUserId(user.getId()).get().getId();
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));
        return new InstructorDTO(instructor);
    }

    public List<InstructorDTO> getAllInstructors() {
        return instructorRepository.findAll().stream()
                .map(instructor -> new InstructorDTO(instructor))
                .collect(Collectors.toList());
    }

    public Page<InstructorDTO> getInstructors(Pageable pageable) {
        return instructorRepository.findAll(pageable).map(InstructorDTO::new);
    }

    public InstructorDTO updateInstructor(User user, InstructorRequestDTO instructorDTO) {
        Integer instructorId = instructorRepository.findByUserId(user.getId()).get().getId();
        Instructor existingInstructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));

        modelMapper.map(instructorDTO, existingInstructor);

        existingInstructor.setUser(user);

        Instructor updatedInstructor = instructorRepository.save(existingInstructor);
        return new InstructorDTO(updatedInstructor);
    }

    public void deleteInstructor(Integer id) {
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        if (instructor.getUser() != null) {
            User user = instructor.getUser();

            // Delete access tokens first
            // accessTokenRepository.deleteByUserId(user.getId());

            // Break the bidirectional relationship
            user.setInstructor(null);
            userRepository.save(user);
        }
        // Delete the student
        instructorRepository.delete(instructor);
        if (instructor.getUser() != null) {
            // Delete the user (now that access tokens are removed)
            userRepository.delete(instructor.getUser());
        }
    }

    // Additional methods specific to Instructor if needed
    public InstructorDTO getInstructorById(Integer id) {
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + id));
        return new InstructorDTO(instructor);
    }

    public CourseDTO addInstructorToCourse(Integer courseId, Integer instructorId, User user)
            throws AccessDeniedException {
        Organization organization = organizationRepository.findByUserId(user.getId());
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        // Verify the requesting organization owns the course
        if (!organization.getId().equals(course.getOrg().getId())) {
            throw new AccessDeniedException("Your organization doesn't own this course");
        }

        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));

        course.addInstructor(instructor);
        Course updatedCourse = courseRepository.save(course);
        return new CourseDTO(updatedCourse);
    }

    public CourseDTO removeInstructorFromCourse(Integer courseId, Integer instructorId, User user)
            throws AccessDeniedException {
        Organization organization = organizationRepository.findByUserId(user.getId());
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        // Verify the requesting organization owns the course
        if (!organization.getId().equals(course.getOrg().getId())) {
            throw new AccessDeniedException("Your organization doesn't own this course");
        }

        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));

        course.removeInstructor(instructor);
        Course updatedCourse = courseRepository.save(course);
        return new CourseDTO(updatedCourse);
    }

    public List<CourseDTO> getCoursesByInstructorId(Integer instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));

        return instructor.getCourses().stream()
                .map(CourseInstructor::getCourse)
                .map(course -> new CourseDTO(course))
                .collect(Collectors.toList());
    }

    public Page<CourseDTO> getMyCourses(User user, Pageable pageable) {
        Instructor instructor = instructorRepository.findById(user.getInstructor().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + user.getInstructor().getId()));
        
        return courseRepository.findByInstructorId(instructor.getId(), pageable)
                .map(CourseDTO::new);

        // return instructor.getCourses().stream()
        //         .map(CourseInstructor::getCourse)
        //         .map(course -> new CourseDTO(course))
        //         .collect(Collectors.toList());
    }

    public List<InstructorDTO> getInstructorsByCourseId(Integer courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        return course.getInstructors().stream()
                .map(CourseInstructor::getInstructor)
                .map(instructor -> new InstructorDTO(instructor))
                .collect(Collectors.toList());
    }

    public long countInstructors() {
        return instructorRepository.count();
    }
}
