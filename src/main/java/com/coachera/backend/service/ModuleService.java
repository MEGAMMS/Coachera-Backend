package com.coachera.backend.service;

import com.coachera.backend.dto.ModuleDTO;
import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Instructor;
import com.coachera.backend.entity.Module;
import com.coachera.backend.entity.Section;
import com.coachera.backend.entity.User;
import com.coachera.backend.exception.DuplicateOrderIndexException;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.CourseRepository;
import com.coachera.backend.repository.InstructorRepository;
import com.coachera.backend.repository.ModuleRepository;
import com.coachera.backend.repository.SectionRepository;
import com.coachera.backend.repository.UserRepository;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;

    private final UserRepository userRepository;
    private final InstructorRepository instructorRepository;

    public ModuleDTO createModule(Integer courseId, ModuleDTO moduleDTO) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        validateOrderIndexUniqueness(courseId, moduleDTO.getOrderIndex());
        Module module = new Module();
        module.setCourse(course);
        module.setOrderIndex(moduleDTO.getOrderIndex());

        Module savedModule = moduleRepository.save(module);
        return new ModuleDTO(savedModule);
    }

    public ModuleDTO updateModule(Integer moduleId, ModuleDTO moduleDTO,User user) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + moduleId));

        validateOrderIndexUniqueness(module.getCourse().getId(), moduleDTO.getOrderIndex());
        module.setOrderIndex(moduleDTO.getOrderIndex());

        if (!isInstructorOfCourse(user, module.getCourse())) {
            throw new IllegalArgumentException("Instructor is not assigned to this course");
        }

        if (moduleDTO.getSectionIds() != null) {
            moduleDTO.getSectionIds().forEach(sectionId -> {
                Section section = sectionRepository.findById(sectionId)
                        .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with ID: " + sectionId));
                module.addSection(section);
            });
        }

        Module updatedModule = moduleRepository.save(module);
        return new ModuleDTO(updatedModule);
    }

    public ModuleDTO getModuleById(Integer moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + moduleId));
        return new ModuleDTO(module);
    }

    public List<ModuleDTO> getAllModulesByCourseId(Integer courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with id: " + courseId);
        }
        List<Module> modules = moduleRepository.findByCourseIdOrderByOrderIndexAsc(courseId);
        return modules.stream()
                .map(ModuleDTO::new)
                .collect(Collectors.toList());
    }

    public void deleteModule(Integer moduleId, User user) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + moduleId));

        if (!isInstructorOfCourse(user, module.getCourse())) {
            throw new IllegalArgumentException("Instructor is not assigned to this course");
        }

        moduleRepository.delete(module);
    }

    private void validateOrderIndexUniqueness(Integer courseId, Integer orderIndex) {
        if (moduleRepository.existsByCourseIdAndOrderIndex(courseId, orderIndex)) {
            throw new DuplicateOrderIndexException(
                    "Order index " + orderIndex + " already exists in this course");
        }
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

}