package com.coachera.backend.repository;

import com.coachera.backend.entity.Video;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Optional<Video> findByUuidName(String uuidName);

    boolean existsByUuidName(String uuidName);

    /**
     * Finds a video by its full URL by extracting the UUID from the URL
     * 
     * @param url The full video URL (e.g.,
     *            "http://localhost:8080/videos/abc123-xyz456.mp4")
     * @return Optional containing the Video if found
     */
    default Optional<Video> findByUrl(String url) {
        try {
            String uuid = Video.extractUuidFromUrl(url);
            return findByUuidName(uuid);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    /**
     * Checks if a video exists by its full URL
     * 
     * @param url The full video URL
     * @return true if video exists, false otherwise
     */
    default boolean existsByUrl(String url) {
        try {
            String uuid = Video.extractUuidFromUrl(url);
            return existsByUuidName(uuid);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
} 