package com.coachera.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coachera.backend.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin,Integer> {
    Optional<Admin> FindById(Integer userId);
}
