package com.coachera.backend.repository;

import com.coachera.backend.entity.Image;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByUuidName(String uuidName);

    boolean existsByUuidName(String uuidName);

    List<Image> findByUuidNameStartingWith(String prefix);

    void deleteByUuidNameStartingWith(String prefix);

    /**
     * Finds an image by its full URL by extracting the UUID from the URL
     * 
     * @param url The full image URL (e.g.,
     *            "http://localhost:8080/images/abc123-xyz456")
     * @return Optional containing the Image if found
     */
    default Optional<Image> findByUrl(String url) {
        try {
            String uuid = Image.extractUuidFromUrl(url);
            return findByUuidName(uuid);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    /**
     * Checks if an image exists by its full URL
     * 
     * @param url The full image URL
     * @return true if image exists, false otherwise
     */
    default boolean existsByUrl(String url) {
        try {
            String uuid = Image.extractUuidFromUrl(url);
            return existsByUuidName(uuid);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
