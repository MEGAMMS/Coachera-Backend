package com.coachera.backend.service;

import com.coachera.backend.dto.FavoriteDTO;
import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Favorite;
import com.coachera.backend.entity.Student;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.CourseRepository;
import com.coachera.backend.repository.FavoriteRepository;
import com.coachera.backend.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public List<FavoriteDTO> getFavoritesByStudentId(Integer studentId) {
        return favoriteRepository.findByStudentId(studentId)
                .stream()
                .map(FavoriteDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public FavoriteDTO addFavorite(Integer studentId, Integer courseId) {
        // Check if already favorited
        if (favoriteRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new IllegalStateException("Course is already favorited by this student");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        Favorite favorite = new Favorite();
        favorite.setStudent(student);
        favorite.setCourse(course);
        
        Favorite savedFavorite = favoriteRepository.save(favorite);
        return new FavoriteDTO(savedFavorite);
    }

    @Transactional
    public void removeFavorite(Integer studentId, Integer courseId) {
        if (!favoriteRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new ResourceNotFoundException("Favorite not found for studentId: " + studentId + " and courseId: " + courseId);
        }
        favoriteRepository.deleteByStudentIdAndCourseId(studentId, courseId);
    }

    public boolean isCourseFavoritedByStudent(Integer studentId, Integer courseId) {
        return favoriteRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }
}