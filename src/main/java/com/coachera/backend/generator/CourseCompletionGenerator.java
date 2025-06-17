package com.coachera.backend.generator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.instancio.Instancio;
import org.instancio.Select;

import com.coachera.backend.entity.CourseCompletion;
import com.coachera.backend.entity.Enrollment;
import com.coachera.backend.entity.MaterialCompletion;

public class CourseCompletionGenerator {
    private static final BigDecimal COMPLETION_THRESHOLD = new BigDecimal("0.95"); // 95% considered complete

    public static List<CourseCompletion> forEnrollmentsWithMaterialProgress(List<Enrollment> enrollments) {
        return enrollments.stream()
                .map(enrollment -> {
                    // Calculate progress based on material completions
                    BigDecimal progress = calculateProgressFromMaterials(enrollment);
                    boolean isCompleted = progress.compareTo(COMPLETION_THRESHOLD) >= 0;
                    LocalDateTime completionDate = isCompleted ? determineCompletionDate(enrollment, progress) : null;

                    return Instancio.of(CourseCompletion.class)
                            .ignore(Select.field(CourseCompletion::getId))
                            .supply(Select.field(CourseCompletion::getEnrollment), () -> enrollment)
                            .supply(Select.field(CourseCompletion::getProgress), () -> progress)
                            .supply(Select.field(CourseCompletion::isCompleted), () -> isCompleted)
                            .supply(Select.field(CourseCompletion::getCompletionDate), () -> completionDate)
                            .create();
                })
                .collect(Collectors.toList());
    }

    private static BigDecimal calculateProgressFromMaterials(Enrollment enrollment) {
        if (enrollment.getMaterialCompletions() == null || enrollment.getMaterialCompletions().isEmpty()) {
            return BigDecimal.ZERO;
        }

        long totalMaterials = enrollment.getCourse().getModules().stream()
                .flatMap(module -> module.getSections().stream())
                .flatMap(section -> section.getMaterials().stream())
                .count();

        if (totalMaterials == 0) {
            return BigDecimal.ZERO;
        }

        long completedMaterials = enrollment.getMaterialCompletions().stream()
                .filter(MaterialCompletion::isCompleted)
                .count();

        return BigDecimal.valueOf(completedMaterials)
                .divide(BigDecimal.valueOf(totalMaterials), 2, RoundingMode.HALF_UP);
    }

    private static LocalDateTime determineCompletionDate(Enrollment enrollment, BigDecimal progress) {
        // Use the latest material completion date if available
        if (enrollment.getMaterialCompletions() != null && !enrollment.getMaterialCompletions().isEmpty()) {
            return enrollment.getMaterialCompletions().stream()
                    .filter(MaterialCompletion::isCompleted)
                    .map(MaterialCompletion::getCompletionDate)
                    .max(LocalDateTime::compareTo)
                    .orElseGet(() -> generateEstimatedDate(enrollment, progress));
        }
        return generateEstimatedDate(enrollment, progress);
    }

    private static LocalDateTime generateEstimatedDate(Enrollment enrollment, BigDecimal progress) {
        LocalDateTime startDate = enrollment.getCreatedAt() != null ? enrollment.getCreatedAt()
                : LocalDateTime.now().minusDays(30);

        // Simulate that higher progress took longer to achieve
        int daysToAdd = (int) (30 * progress.doubleValue());
        return startDate.plusDays(daysToAdd);
    }

    // Helper method to update existing enrollments with calculated completions
    public static void updateCompletionsFromMaterials(List<Enrollment> enrollments) {
        enrollments.forEach(enrollment -> {
            if (enrollment.getCourseCompletion() == null) {
                enrollment.setCourseCompletion(new CourseCompletion());
                enrollment.getCourseCompletion().setEnrollment(enrollment);
            }

            BigDecimal progress = calculateProgressFromMaterials(enrollment);
            enrollment.getCourseCompletion().setProgress(progress);
            enrollment.getCourseCompletion().setCompleted(progress.compareTo(COMPLETION_THRESHOLD) >= 0);

            if (enrollment.getCourseCompletion().isCompleted() &&
                    enrollment.getCourseCompletion().getCompletionDate() == null) {
                enrollment.getCourseCompletion().setCompletionDate(
                        determineCompletionDate(enrollment, progress));
            }
        });
    }
}