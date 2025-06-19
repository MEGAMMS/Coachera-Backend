package com.coachera.backend.service;

import com.coachera.backend.entity.*;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.EnrollmentRepository;
import com.coachera.backend.repository.MaterialRepository;
import com.coachera.backend.repository.VideoViewingRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VideoViewingService {
    private final VideoViewingRepository videoViewingRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final MaterialRepository materialRepository;

    /**
     * Records a video viewing event
     */
    public void recordViewing(User user, Integer materialId, double percentWatched) {

        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found with id: " + materialId));

        Integer studentId = user.getStudent().getId();
        Integer courseId = material.getSection().getModule().getCourse().getId();
        
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Enrollment not found for student: " + studentId + " in course " + courseId));

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