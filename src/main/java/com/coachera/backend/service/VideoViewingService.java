package com.coachera.backend.service;

import com.coachera.backend.entity.*;
import com.coachera.backend.repository.VideoViewingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VideoViewingService {
    private final VideoViewingRepository videoViewingRepository;
    
    public VideoViewingService(VideoViewingRepository videoViewingRepository) {
        this.videoViewingRepository = videoViewingRepository;
    }

    /**
     * Records a video viewing event
     */
    public void recordViewing(Enrollment enrollment, Material material, double percentWatched) {
        VideoViewing viewing = videoViewingRepository
            .findByEnrollmentAndVideo(enrollment, material.getVideo())
            .orElseGet(() -> {
                VideoViewing newViewing = new VideoViewing();
                newViewing.setEnrollment(enrollment);
                newViewing.setVideo(material.getVideo());
                return newViewing;
            });

        viewing.setLastViewed(LocalDateTime.now());
        viewing.setPercentWatched(Math.max(percentWatched, viewing.getPercentWatched()));
        videoViewingRepository.save(viewing);
    }

    /**
     * Checks if video is considered "viewed" based on criteria
     */
    public boolean isViewed(Enrollment enrollment, Material material) {
        // You could make these criteria configurable per course/material
        final double COMPLETION_THRESHOLD = 0.9; // 90% watched
        
        return videoViewingRepository.findByEnrollmentAndVideo(enrollment, material.getVideo())
            .map(viewing -> viewing.getPercentWatched() >= COMPLETION_THRESHOLD)
            .orElse(false);
    }
}