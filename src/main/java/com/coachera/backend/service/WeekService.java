// WeekService.java
package com.coachera.backend.service;

import com.coachera.backend.dto.WeekDTO;
import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Week;
import com.coachera.backend.exception.DuplicateOrderIndexException;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.CourseRepository;
import com.coachera.backend.repository.WeekRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WeekService {

    private final WeekRepository weekRepository;
    private final CourseRepository courseRepository;

    public WeekService(WeekRepository weekRepository, CourseRepository courseRepository) {
        this.weekRepository = weekRepository;
        this.courseRepository = courseRepository;
    }

    public WeekDTO createWeek(Integer courseId, WeekDTO weekDTO) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        validateOrderIndexUniqueness(courseId, weekDTO.getOrderIndex());
        Week week = new Week();
        week.setCourse(course);
        week.setOrderIndex(weekDTO.getOrderIndex());
        
        Week savedWeek = weekRepository.save(week);
        return new WeekDTO(savedWeek);
    }

    public WeekDTO updateWeek(Integer weekId, WeekDTO weekDTO) {
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new ResourceNotFoundException("Week not found with id: " + weekId));
        
        validateOrderIndexUniqueness(week.getCourse().getId(), weekDTO.getOrderIndex());
        week.setOrderIndex(weekDTO.getOrderIndex());
        
        Week updatedWeek = weekRepository.save(week);
        return new WeekDTO(updatedWeek);
    }

   
    public WeekDTO getWeekById(Integer weekId) {
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new ResourceNotFoundException("Week not found with id: " + weekId));
        return new WeekDTO(week);
    }

    
    public List<WeekDTO> getAllWeeksByCourseId(Integer courseId) {
        if (!courseRepository.existsById(courseId)) {
        throw new ResourceNotFoundException("Course not found with id: " + courseId);
    }
        List<Week> weeks = weekRepository.findByCourseIdOrderByOrderIndexAsc(courseId);
        return weeks.stream()
                .map(WeekDTO::new)
                .collect(Collectors.toList());
    }

    public void deleteWeek(Integer weekId) {
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new ResourceNotFoundException("Week not found with id: " + weekId));
        weekRepository.delete(week);
    }

    private void validateOrderIndexUniqueness(Integer courseId, Integer orderIndex) {
        if (weekRepository.existsByCourseIdAndOrderIndex(courseId, orderIndex)) {
            throw new DuplicateOrderIndexException(
                "Order index " + orderIndex + " already exists in this course");
        }
    }
}