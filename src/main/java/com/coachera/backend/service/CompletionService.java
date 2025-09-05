package com.coachera.backend.service;

import com.coachera.backend.dto.CourseCompletionDTO;
import com.coachera.backend.dto.MaterialCompletionDTO;
import com.coachera.backend.entity.*;
import com.coachera.backend.entity.enums.CompletionState;
import com.coachera.backend.entity.enums.CompletionTriggerType;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
// @RequiredArgsConstructor
public class CompletionService {

    private final MaterialCompletionRepository materialCompletionRepository;
    private final CourseCompletionRepository courseCompletionRepository;
    private final MaterialRepository materialRepository;
    private final QuizVerificationService quizService;
    private final EnrollmentRepository enrollmentRepository;

    public CompletionService(
            MaterialCompletionRepository materialCompletionRepository,
            CourseCompletionRepository courseCompletionRepository,
            MaterialRepository materialRepository,
            QuizVerificationService quizService,
            EnrollmentRepository enrollmentRepository) {
        this.materialCompletionRepository = materialCompletionRepository;
        this.courseCompletionRepository = courseCompletionRepository;
        this.materialRepository = materialRepository;
        this.quizService = quizService;
        this.enrollmentRepository = enrollmentRepository;
    }

    /**
     * Checks and updates material completion status based on the material type
     */
    @Transactional
    public void checkMaterialCompletion(Integer enrollmentId, Integer materialId) {
        boolean completed = false;
        CompletionState state = CompletionState.INCOMPLETE;
        CompletionTriggerType triggerType = null;

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found with id: " + materialId));

        switch (material.getType()) {
            case VIDEO:
            case ARTICLE:
                markMaterialComplete(enrollmentId, materialId);
                break;
            case QUIZ:
                completed = quizService.isQuizPassed(enrollment, material);
                state = completed ? CompletionState.COMPLETE_PASS : CompletionState.COMPLETE_FAIL;
                triggerType = CompletionTriggerType.GRADE;
                break;
        }

        if (completed) {
            MaterialCompletion completion = materialCompletionRepository
                    .findByEnrollmentAndMaterial(enrollment, material)
                    .orElseGet(() -> {
                        MaterialCompletion newCompletion = new MaterialCompletion();
                        newCompletion.setEnrollment(enrollment);
                        newCompletion.setMaterial(material);
                        return newCompletion;
                    });

            completion.setCompleted(true);
            completion.setCompletionState(state);
            completion.setCompletionDate(LocalDateTime.now());
            completion.setTriggerType(triggerType);
            materialCompletionRepository.save(completion);

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
        if (totalMaterials == 0)
            return;

        long completedMaterials = materialCompletionRepository.countByEnrollmentAndCompleted(enrollment, true);
        BigDecimal progress = BigDecimal.valueOf((double) completedMaterials / totalMaterials * 100)
                .setScale(2, RoundingMode.HALF_UP);
        boolean isCompleted = progress.compareTo(BigDecimal.valueOf(100)) >= 0;

        CourseCompletion completion = courseCompletionRepository.findByEnrollment(enrollment)
                .orElseGet(() -> {
                    CourseCompletion newCompletion = new CourseCompletion();
                    newCompletion.setEnrollment(enrollment);
                    return newCompletion;
                });

        completion.setProgress(progress);
        completion.setCompleted(isCompleted);
        if (isCompleted) {
            completion.setCompletionDate(LocalDateTime.now());
        }
        courseCompletionRepository.save(completion);
    }

    /**
     * Manually mark a material as complete for a student
     */
    @Transactional
    public MaterialCompletionDTO markMaterialComplete(Integer enrollmentId, Integer materialId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found with id: " + materialId));

        MaterialCompletion completion = new MaterialCompletion();
        completion.setEnrollment(enrollment);
        completion.setMaterial(material);
        completion.setCompleted(true);
        completion.setCompletionState(CompletionState.COMPLETE);
        completion.setCompletionDate(LocalDateTime.now());
        completion.setTriggerType(CompletionTriggerType.MANUAL);

        materialCompletionRepository.save(completion);
        updateCourseProgress(enrollment);
        return new MaterialCompletionDTO(completion);
    }

    /**
     * Get completion status for all materials in a course for a student
     */
    @Transactional
    public List<MaterialCompletionDTO> getMaterialCompletions(Integer enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

        return materialCompletionRepository.findByEnrollment(enrollment).stream()
                .map(MaterialCompletionDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get course completion status for a student
     */
    @Transactional
    public CourseCompletionDTO getCourseCompletion(Integer enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

        CourseCompletion courseCompletion = courseCompletionRepository.findByEnrollment(enrollment)
                .orElseThrow(() -> new ResourceNotFoundException("course not started yet" + enrollment));

        return new CourseCompletionDTO(courseCompletion);
    }

    /**
     * Get completions status for a course
     */
    @Transactional
    public List<CourseCompletionDTO> getCompletionsByCourse(Integer courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);

        List<CourseCompletionDTO> completionDTOs = new ArrayList<>();

        for (Enrollment enrollment : enrollments) {

            CourseCompletion courseCompletion = courseCompletionRepository.findByEnrollment(enrollment)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Course completion not found for enrollment: " + enrollment.getId()));

            completionDTOs.add(new CourseCompletionDTO(courseCompletion));
        }

        return completionDTOs;
    }

    /**
     * Get courses completion status for a student
     */
    @Transactional
    public List<CourseCompletionDTO> getCompletionsByStudent(Integer studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);

        return enrollments.stream()
                .map(enrollment -> courseCompletionRepository.findByEnrollment(enrollment)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Course completion not found for enrollment: " + enrollment.getId())))
                .map(CourseCompletionDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Resets all completion progress for a given enrollment
     * - Deletes all material completions
     * - Resets course completion progress to 0
     */
    @Transactional
    public void resetCompletion(Integer enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

        // Delete all material completions for this enrollment
        materialCompletionRepository.deleteByEnrollment(enrollment);

        // Reset course completion
        CourseCompletion courseCompletion = courseCompletionRepository.findByEnrollment(enrollment)
                .orElseGet(() -> {
                    CourseCompletion newCompletion = new CourseCompletion();
                    newCompletion.setEnrollment(enrollment);
                    return newCompletion;
                });

        courseCompletion.setProgress(BigDecimal.ZERO);
        courseCompletion.setCompleted(false);
        courseCompletion.setCompletionDate(null);
        courseCompletionRepository.save(courseCompletion);
    }
}
