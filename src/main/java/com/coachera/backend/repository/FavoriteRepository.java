package com.coachera.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coachera.backend.entity.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    List<Favorite> findByStudentId(Integer studentId);
    Optional<Favorite> findByCourseIdAndStudentId(Integer courseId, Integer studentId);
}
