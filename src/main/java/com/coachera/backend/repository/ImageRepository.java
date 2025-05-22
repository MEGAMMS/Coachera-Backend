package com.coachera.backend.repository;

import com.coachera.backend.entity.Image;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByUuidName(String uuidName);

    boolean existsByUuidName(String uuidName);
}
