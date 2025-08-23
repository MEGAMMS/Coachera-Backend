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

@Service
@Transactional
public class InstructorService {

    private final InstructorRepository instructorRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final CourseRepository courseRepository;
    private final OrganizationRepository organizationRepository;
    

    public InstructorService(InstructorRepository instructorRepository, 
                           UserRepository userRepository,
                           ModelMapper modelMapper,
                           CourseRepository courseRepository,
                           OrganizationRepository organizationRepository) {
        this.instructorRepository = instructorRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.courseRepository = courseRepository;
        this.organizationRepository = organizationRepository;
    }

    public InstructorDTO createInstructor(InstructorDTO instructorDTO, User user) {
        if (instructorRepository.existsByUserId(instructorDTO.getUserId())) {
            throw new ConflictException("User already has an instructor profile");
        }
        
        Instructor instructor = new Instructor();
        instructor.setUser(user);
        instructor.setFullname(instructorDTO.getFullname());
        instructor.setBio(instructorDTO.getBio());
        
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
        return instructorRepository.findAll(pageable).
                map(InstructorDTO::new);
    }

    public InstructorDTO updateInstructor(User user, InstructorDTO instructorDTO) {
        Integer instructorId = instructorRepository.findByUserId(user.getId()).get().getId();
        Instructor existingInstructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));

        modelMapper.map(instructorDTO, existingInstructor);
        
        if (instructorDTO.getUserId() != null && 
            !instructorDTO.getUserId().equals(existingInstructor.getUser().getId())) {
            User newUser = userRepository.findById(instructorDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + instructorDTO.getUserId()));
            existingInstructor.setUser(newUser);
        }

        Instructor updatedInstructor = instructorRepository.save(existingInstructor);
        return new InstructorDTO(updatedInstructor);
    }

    public void deleteInstructor(Integer id) {
        if (!instructorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Instructor not found with id: " + id);
        }
        instructorRepository.deleteById(id);
    }
    
    // Additional methods specific to Instructor if needed
    public InstructorDTO getInstructorById(Integer id) {
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + id));
        return new InstructorDTO(instructor);
    }

    public CourseDTO addInstructorToCourse(Integer courseId, Integer instructorId, User user) throws AccessDeniedException {
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

    public CourseDTO removeInstructorFromCourse(Integer courseId, Integer instructorId, User user) throws AccessDeniedException {
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