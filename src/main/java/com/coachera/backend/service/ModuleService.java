// ModuleService.java
package com.coachera.backend.service;

import com.coachera.backend.dto.ModuleDTO;
import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Module;
import com.coachera.backend.exception.DuplicateOrderIndexException;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.CourseRepository;
import com.coachera.backend.repository.ModuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;

    public ModuleService(ModuleRepository moduleRepository, CourseRepository courseRepository) {
        this.moduleRepository = moduleRepository;
        this.courseRepository = courseRepository;
    }

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

    public ModuleDTO updateModule(Integer moduleId, ModuleDTO moduleDTO) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + moduleId));
        
        validateOrderIndexUniqueness(module.getCourse().getId(), moduleDTO.getOrderIndex());
        module.setOrderIndex(moduleDTO.getOrderIndex());
        
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

    public void deleteModule(Integer moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + moduleId));
        moduleRepository.delete(module);
    }

    private void validateOrderIndexUniqueness(Integer courseId, Integer orderIndex) {
        if (moduleRepository.existsByCourseIdAndOrderIndex(courseId, orderIndex)) {
            throw new DuplicateOrderIndexException(
                "Order index " + orderIndex + " already exists in this course");
        }
    }
}