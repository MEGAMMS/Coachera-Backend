package com.coachera.backend.repository;

import com.coachera.backend.entity.Enrollment;
import com.coachera.backend.entity.Video;
import com.coachera.backend.entity.VideoViewing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoViewingRepository extends JpaRepository<VideoViewing, Long> {
    Optional<VideoViewing> findByEnrollmentAndVideo(Enrollment enrollment,Video video);
}