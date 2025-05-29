package com.coachera.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coachera.backend.entity.Student;
import com.coachera.backend.entity.User;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    Student findByUserId(Integer userId);

    boolean existsByUser(User user);

    boolean existsByUserId(Integer userId);

}
