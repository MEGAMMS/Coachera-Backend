package com.coachera.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coachera.backend.entity.Student;
import com.coachera.backend.entity.User;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByUserId(Integer userId);
    
     boolean existsByUser(User user);
    
     boolean existsByUserId(Integer userId);
    
}
