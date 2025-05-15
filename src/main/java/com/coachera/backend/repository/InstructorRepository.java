package com.coachera.backend.repository;

import com.coachera.backend.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Integer> {

    // Find instructor by associated user ID
    Optional<Instructor> findByUserId(Integer userId);

    // Check if an instructor exists for a given user ID
    boolean existsByUserId(Integer userId);

    // Find instructor by user email (assuming User entity has email field)
    Optional<Instructor> findByUserEmail(String email);

    // Custom query to find instructors with bio containing keyword (case insensitive)
    List<Instructor> findByBioContainingIgnoreCase(String keyword);
}