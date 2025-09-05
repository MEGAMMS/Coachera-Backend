package com.coachera.backend.service;

import com.coachera.backend.dto.CourseCreationDTO;
import com.coachera.backend.dto.CourseDTO;
import com.coachera.backend.dto.CourseWithModulesDTO;
import com.coachera.backend.entity.Category;
import com.coachera.backend.entity.Certificate;
import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Image;
import com.coachera.backend.entity.Instructor;
import com.coachera.backend.entity.Organization;
import com.coachera.backend.entity.User;
import com.coachera.backend.exception.ConflictException;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.CategoryRepository;
import com.coachera.backend.repository.CertificateRepository;
import com.coachera.backend.repository.CourseRepository;
import com.coachera.backend.repository.EnrollmentRepository;
import com.coachera.backend.repository.InstructorRepository;
import com.coachera.backend.repository.OrganizationRepository;
import com.coachera.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final OrganizationRepository organizationRepository;
    private final InstructorRepository instructorRepository;
    private final CategoryRepository categoryRepository;
    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    private final ModelMapper modelMapper;

    private final ImageService imageService;

    public CourseDTO createCourse(CourseCreationDTO courseDTO, User user) {
        Organization org = organizationRepository.findByUserId(user.getId());
        if (courseRepository.existsByTitleAndOrgId(courseDTO.getTitle(), org.getId())) {
            throw new ConflictException("Course with this title already exists in the organization");
        }

        System.out.println("------------------org------------------");

        Course course = new Course();
        course.setTitle(courseDTO.getTitle()); // System.out.println("------------------title------------------");
        course.setDescription(courseDTO.getDescription()); // System.out.println("------------------descreption------------------");
        course.setDurationHours(courseDTO.getDurationHours()); // System.out.println("------------------dh------------------");
        course.setPrice(courseDTO.getPrice()); // System.out.println("------------------price------------------");
        course.setRating(BigDecimal.valueOf(0)); // System.out.println("------------------rating------------------");

        if (courseDTO.getImageUrl() != null) {
            Image image = imageService.getImageFromUrl(courseDTO.getImageUrl());
            course.setImage(image);
        }

        if (courseDTO.getCategories() != null) {
            Set<Category> categoryEntities = courseDTO.getCategories().stream()
                    .map(catName -> categoryRepository.findByName(catName) // check if it exists
                            .orElseGet(() -> {
                                Category newCategory = new Category();
                                newCategory.setName(catName);
                                return categoryRepository.save(newCategory);
                            }))
                    .collect(Collectors.toSet());

            course.addCategories(categoryEntities);
        }

        System.out.println("------------------cat------------------");

        // if instructors is a list of user IDs, you must fetch them
        if (courseDTO.getInstructors() != null) {
            courseDTO.getInstructors().forEach(instructorId -> {
                Instructor instructor = instructorRepository.findById(instructorId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Instructor not found with ID: " + instructorId));
                course.addInstructor(instructor);
            });
        }

        System.out.println("------------------inst------------------");

        course.setIsPublished(false);
        course.setOrg(org);

        Course savedCourse = courseRepository.save(course);

        CourseDTO payload = new CourseDTO(savedCourse);

        // System.out.println(payload);
        return payload;
    }

    public CourseWithModulesDTO getCourseById(Integer id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        CourseWithModulesDTO courseDTO = new CourseWithModulesDTO(course);
        return courseDTO;
    }

    public Page<CourseDTO> getCoursesByOrganization(Integer orgId, Pageable pageable) {
        return courseRepository.findByOrgIdAndIsPublishedTrue(orgId, pageable)
                .map(CourseDTO::new);
    }

    public CourseDTO updateCourse(Integer id, CourseDTO courseDTO) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        if (!existingCourse.getTitle().equals(courseDTO.getTitle()) &&
                courseRepository.existsByTitleAndOrgId(courseDTO.getTitle(), courseDTO.getOrgId())) {
            throw new ConflictException("Course title already exists in this organization");
        }

        if (courseDTO.getOrgId() != null &&
                !courseDTO.getOrgId().equals(existingCourse.getOrg().getId())) {
            Organization org = organizationRepository.findById(courseDTO.getOrgId())
                    .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
            existingCourse.setOrg(org);
        }

        modelMapper.map(courseDTO, existingCourse);
        existingCourse.setIsPublished(false);
        Course updatedCourse = courseRepository.save(existingCourse);
        return new CourseDTO(updatedCourse);
    }

    public void deleteCourse(Integer id, User user) {
        Organization org = validateOrg(user);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (!course.getOrg().getId().equals(org.getId())) {
            throw new AccessDeniedException("You are not allowed to delete this course");
        }

        if (enrollmentRepository.existsByCourse(course)) {
            Organization coachera = organizationRepository.findById(1)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Coachera not found with id"));
            course.setOrg(coachera);
            course.setIsPublished(false);
        } else {

            // Break relation with certificates
            List<Certificate> certificates = certificateRepository.findByCourseId(id);
            for (Certificate certificate : certificates) {
                certificate.setCourse(null);
                certificateRepository.save(certificate);
            }

            courseRepository.deleteById(id);
        }

    }

    public Page<CourseDTO> getCoursesPublished(Pageable pageable) {
        return courseRepository.findByIsPublishedTrue(pageable)
                .map(CourseDTO::new);
    }

    public Page<CourseDTO> getCourses(Pageable pageable) {
        return courseRepository.findAll(pageable)
                .map(CourseDTO::new);
    }

    public void publishCourse(Integer id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        course.setIsPublished(true); // Assumes your Course entity has a setIsPublished method
        courseRepository.save(course);
    }

    public void unpublishCourse(Integer id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        course.setIsPublished(false); // Assumes your Course entity has a setIsPublished method
        courseRepository.save(course);
    }

    // Helper method
    private Organization validateOrg(User user) {
        // Check if user exists
        userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User must be saved before creating Org profile"));

        // Check if organization exists
        return organizationRepository.findById(user.getOrganization().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Organization not found with id: " + user.getOrganization().getId()));
    }
}
