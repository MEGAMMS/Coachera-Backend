package com.coachera.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coachera.backend.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByCourseId(Integer courseId);
    List<Review> findByStudentId(Integer studentId);
    boolean existsByCourseIdAndStudentId(Integer courseId, Integer studentId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.course.id = :courseId")
    Double calculateAverageRatingByCourseId(@Param("courseId") Integer courseId);
}