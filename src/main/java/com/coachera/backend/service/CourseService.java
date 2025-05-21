package com.coachera.backend.service;

import com.coachera.backend.dto.CourseDTO;
import com.coachera.backend.dto.OrganizationDTO;
import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Organization;
import com.coachera.backend.entity.User;
import com.coachera.backend.exception.ConflictException;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.CourseRepository;
import com.coachera.backend.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final OrganizationRepository organizationRepository;
    private final ModelMapper modelMapper;

    public CourseDTO createCourse(CourseDTO courseDTO ,User user) {   
        Organization org =organizationRepository.findByUserId(user.getId());
        if (courseRepository.existsByTitleAndOrgId(courseDTO.getTitle(),org.getId())) {
            throw new ConflictException("Course with this title already exists in the organization");
        }

        Course course = modelMapper.map(courseDTO, Course.class);
        course.setOrg(org);

        Course savedCourse = courseRepository.save(course);
        CourseDTO courseDTO2=new CourseDTO(savedCourse);
        return courseDTO2;
    }

    public CourseDTO getCourseById(Integer id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        CourseDTO courseDTO=new CourseDTO(course);
        return courseDTO;
    }

    public List<CourseDTO> getCoursesByOrganization(Integer orgId) {
        return courseRepository.findByOrgId(orgId).stream()
                .map(course -> new CourseDTO(course))
                .toList();
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
        Course updatedCourse = courseRepository.save(existingCourse);
        return modelMapper.map(updatedCourse, CourseDTO.class);
    }

    public void deleteCourse(Integer id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found");
        }
        courseRepository.deleteById(id);
    }

    public List<CourseDTO> getACourses()
    {
        return courseRepository.findAll()
            .stream()
            .map(org -> modelMapper.map(org, CourseDTO.class))
            .toList();
    }
}