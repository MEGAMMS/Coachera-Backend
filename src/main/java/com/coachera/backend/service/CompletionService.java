package com.coachera.backend.service;

import com.coachera.backend.dto.CourseCompletionDTO;
import com.coachera.backend.entity.*;
import com.coachera.backend.entity.enums.CompletionState;
import com.coachera.backend.entity.enums.CompletionTriggerType;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.entity.Material.MaterialType;
import com.coachera.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompletionService {

    private final MaterialCompletionRepository materialCompletionRepository;
    private final CourseCompletionRepository courseCompletionRepository;
    private final MaterialRepository materialRepository;
    private final QuizService quizService;
    // private final VideoViewingService videoViewingService;
    private final EnrollmentRepository enrollmentRepository;

    /**
     * Checks and updates material completion status based on the material type
     */
    @Transactional
    public void checkMaterialCompletion(Enrollment enrollment, Material material) {
        boolean completed = false;
        CompletionState state = CompletionState.INCOMPLETE;
        CompletionTriggerType triggerType = null;

        switch (material.getType()) {
            // case VIDEO:
            // completed = videoViewingService.isViewed(enrollment, material);
            // state = completed ? CompletionState.COMPLETE : CompletionState.INCOMPLETE;
            // triggerType = CompletionTriggerType.VIEWING;
            // break;
            // case QUIZ:
            // completed = quizService.isQuizPassed(enrollment, material);
            // state = completed ? CompletionState.COMPLETE_PASS :
            // CompletionState.COMPLETE_FAIL;
            // triggerType = CompletionTriggerType.GRADE;
            // break;
            case ARTICLE:
                // Articles might be marked complete manually or after certain reading time
                // This would require additional tracking
                break;
        }

        if (completed) {
            Optional<MaterialCompletion> existingCompletion = materialCompletionRepository
                    .findByEnrollmentAndMaterial(enrollment, material);

            if (existingCompletion.isPresent()) {
                MaterialCompletion completion = existingCompletion.get();
                completion.setCompleted(true);
                completion.setCompletionState(state);
                completion.setCompletionDate(LocalDateTime.now());
                completion.setTriggerType(triggerType);
                materialCompletionRepository.save(completion);
            } else {
                MaterialCompletion newCompletion = new MaterialCompletion();
                newCompletion.setEnrollment(enrollment);
                newCompletion.setMaterial(material);
                newCompletion.setCompleted(true);
                newCompletion.setCompletionState(state);
                newCompletion.setCompletionDate(LocalDateTime.now());
                newCompletion.setTriggerType(triggerType);
                materialCompletionRepository.save(newCompletion);
            }

            updateCourseProgress(enrollment);
        }
    }

    /**
     * Updates the overall course progress for an enrollment
     */
    @Transactional
    public void updateCourseProgress(Enrollment enrollment) {
        Course course = enrollment.getCourse();
        long totalMaterials = materialRepository.countByCourse(course);
        long completedMaterials = materialCompletionRepository
                .countByEnrollmentAndCompleted(enrollment, true);

        if (totalMaterials > 0) {
            BigDecimal progress = BigDecimal.valueOf((double) completedMaterials / totalMaterials * 100)
                    .setScale(2, RoundingMode.HALF_UP);

            Optional<CourseCompletion> existingCompletion = courseCompletionRepository.findById(enrollment);

            if (existingCompletion.isPresent()) {
                CourseCompletion completion = existingCompletion.get();
                completion.setProgress(progress);
                completion.setCompleted(progress.compareTo(BigDecimal.valueOf(100)) >= 0);
                if (completion.isCompleted()) {
                    completion.setCompletionDate(LocalDateTime.now());
                }
                courseCompletionRepository.save(completion);
            } else {
                CourseCompletion newCompletion = new CourseCompletion();
                newCompletion.setEnrollment(enrollment);
                newCompletion.setProgress(progress);
                newCompletion.setCompleted(progress.compareTo(BigDecimal.valueOf(100)) >= 0);
                if (newCompletion.isCompleted()) {
                    newCompletion.setCompletionDate(LocalDateTime.now());
                }
                courseCompletionRepository.save(newCompletion);
            }
        }
    }

    /**
     * Manually mark a material as complete for a student
     */
    @Transactional
    public MaterialCompletion markMaterialComplete(Enrollment enrollment, Material material) {
        MaterialCompletion completion = new MaterialCompletion();
        completion.setEnrollment(enrollment);
        completion.setMaterial(material);
        completion.setCompleted(true);
        completion.setCompletionState(CompletionState.COMPLETE);
        completion.setCompletionDate(LocalDateTime.now());
        completion.setTriggerType(CompletionTriggerType.MANUAL);

        materialCompletionRepository.save(completion);
        updateCourseProgress(enrollment);
        return completion;
    }

    /**
     * Get completion status for all materials in a course for a student
     */
    public List<MaterialCompletion> getMaterialCompletions(Enrollment enrollment) {
        return materialCompletionRepository.findByEnrollment(enrollment);
    }

    /**
     * Get course completion status for a student
     */
    public CourseCompletionDTO getCourseCompletion(Integer enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));
        
        CourseCompletion courseCompletion = courseCompletionRepository.findById(enrollment)
                .orElseThrow(() -> new ResourceNotFoundException("course not started yet" + enrollment));

        return new CourseCompletionDTO(courseCompletion);
    }
}
