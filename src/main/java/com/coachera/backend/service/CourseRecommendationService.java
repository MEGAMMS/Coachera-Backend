package com.coachera.backend.service;

import com.coachera.backend.dto.CourseDTO;
import com.coachera.backend.entity.*;
import com.coachera.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseRecommendationService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseCategoryRepository courseCategoryRepository;
    private final StudentSkillRepository studentSkillRepository;

    /**
     * Get personalized course recommendations for a user
     */
    public Page<CourseDTO> getRecommendedCourses(User user, Pageable pageable) {
        Student student = studentRepository.findByUserId(user.getId());
        if (student == null) {
            // If user is not a student, return popular courses
            return getPopularCourses(pageable);
        }

        // Get user's enrolled courses
        List<Enrollment> userEnrollments = enrollmentRepository.findByStudentId(student.getId());

        if (userEnrollments.isEmpty()) {
            // If user has no enrollments, return popular courses
            return getPopularCourses(pageable);
        }

        // Get user's preferred categories based on enrollments
        Set<Integer> preferredCategoryIds = getUserPreferredCategories(userEnrollments);

        // Get user's skills
        Set<Integer> userSkillIds = getUserSkillIds(student.getId());

        // Get recommended courses based on preferences and skills
        List<Course> recommendedCourses = getCoursesByPreferencesAndSkills(
                preferredCategoryIds, userSkillIds, userEnrollments);

        // Only published courses
        recommendedCourses = recommendedCourses.stream().filter(Course::getIsPublished).collect(Collectors.toList());

        // Apply pagination manually since we're doing custom scoring
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), recommendedCourses.size());

        if (start >= recommendedCourses.size()) {
            return Page.empty(pageable);
        }

        List<CourseDTO> pageContent = recommendedCourses.subList(start, end).stream()
                .map(CourseDTO::new)
                .collect(Collectors.toList());

        return new org.springframework.data.domain.PageImpl<>(
                pageContent, pageable, recommendedCourses.size());
    }

    /**
     * Get popular courses based on ratings and enrollment count
     */
    public Page<CourseDTO> getPopularCourses(Pageable pageable) {
        List<Course> allCourses = courseRepository.findByIsPublishedTrue();

        List<Course> sortedCourses = allCourses.stream()
                .sorted((c1, c2) -> {
                    // Sort by rating first, then by price (lower price gets preference)
                    int ratingComparison = c2.getRating().compareTo(c1.getRating());
                    if (ratingComparison != 0) {
                        return ratingComparison;
                    }
                    return c1.getPrice().compareTo(c2.getPrice());
                })
                .collect(Collectors.toList());

        // Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sortedCourses.size());

        if (start >= sortedCourses.size()) {
            return Page.empty(pageable);
        }

        List<CourseDTO> pageContent = sortedCourses.subList(start, end).stream()
                .map(CourseDTO::new)
                .collect(Collectors.toList());

        return new org.springframework.data.domain.PageImpl<>(
                pageContent, pageable, sortedCourses.size());
    }

    /**
     * Get user's preferred categories based on enrolled courses
     */
    private Set<Integer> getUserPreferredCategories(List<Enrollment> enrollments) {
        Set<Integer> categoryIds = new HashSet<>();

        for (Enrollment enrollment : enrollments) {
            Course course = enrollment.getCourse();
            if (!course.getIsPublished()) continue;
            List<CourseCategory> courseCategories = courseCategoryRepository.findByCourseId(course.getId());

            for (CourseCategory courseCategory : courseCategories) {
                categoryIds.add(courseCategory.getCategory().getId());
            }
        }

        return categoryIds;
    }

    /**
     * Get user's skill IDs
     */
    private Set<Integer> getUserSkillIds(Integer studentId) {
        return studentSkillRepository.findByStudentId(studentId).stream()
                .map(studentSkill -> studentSkill.getSkill().getId())
                .collect(Collectors.toSet());
    }

    /**
     * Get courses based on user preferences and skills
     */
    private List<Course> getCoursesByPreferencesAndSkills(
            Set<Integer> preferredCategoryIds,
            Set<Integer> userSkillIds,
            List<Enrollment> userEnrollments) {

        // Get all published courses
        List<Course> allCourses = courseRepository.findByIsPublishedTrue();

        // Filter out courses user is already enrolled in
        Set<Integer> enrolledCourseIds = userEnrollments.stream()
                .map(enrollment -> enrollment.getCourse().getId())
                .collect(Collectors.toSet());

        List<Course> availableCourses = allCourses.stream()
                .filter(course -> !enrolledCourseIds.contains(course.getId()))
                .collect(Collectors.toList());

        // Score courses based on preferences
        List<CourseScore> courseScores = availableCourses.stream()
                .map(course -> scoreCourse(course, preferredCategoryIds, userSkillIds))
                .sorted(Comparator.comparing(CourseScore::getScore).reversed())
                .collect(Collectors.toList());

        return courseScores.stream()
                .map(CourseScore::getCourse)
                .collect(Collectors.toList());
    }

    /**
     * Score a course based on user preferences and skills
     */
    private CourseScore scoreCourse(Course course, Set<Integer> preferredCategoryIds, Set<Integer> userSkillIds) {
        double score = 0.0;

        // Base score from course rating (0-5 scale)
        score += course.getRating().doubleValue() * 2.0;

        // Category preference bonus
        List<CourseCategory> courseCategories = courseCategoryRepository.findByCourseId(course.getId());
        for (CourseCategory courseCategory : courseCategories) {
            if (preferredCategoryIds.contains(courseCategory.getCategory().getId())) {
                score += 3.0; // Bonus for preferred category
            }
        }

        // Price factor (lower price gets higher score)
        double priceScore = 100.0 / (course.getPrice().doubleValue() + 1.0);
        score += priceScore * 0.5;

        // Popularity factor (based on enrollment count)
        long enrollmentCount = enrollmentRepository.findByCourseId(course.getId()).size();
        score += Math.min(enrollmentCount * 0.1, 5.0); // Cap at 5 points

        return new CourseScore(course, score);
    }

    /**
     * Get courses by category
     */
    public Page<CourseDTO> getCoursesByCategory(Integer categoryId, Pageable pageable) {
        List<CourseCategory> courseCategories = courseCategoryRepository.findByCategoryId(categoryId);

        List<Course> sortedCourses = courseCategories.stream()
                .map(CourseCategory::getCourse)
                .filter(Course::getIsPublished)
                .sorted((c1, c2) -> c2.getRating().compareTo(c1.getRating()))
                .collect(Collectors.toList());

        // Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sortedCourses.size());

        if (start >= sortedCourses.size()) {
            return Page.empty(pageable);
        }

        List<CourseDTO> pageContent = sortedCourses.subList(start, end).stream()
                .map(CourseDTO::new)
                .collect(Collectors.toList());

        return new org.springframework.data.domain.PageImpl<>(
                pageContent, pageable, sortedCourses.size());
    }

    /**
     * Get trending courses (courses with high recent activity)
     */
    public Page<CourseDTO> getTrendingCourses(Pageable pageable) {
        List<Course> allCourses = courseRepository.findByIsPublishedTrue();

        List<Course> sortedCourses = allCourses.stream()
                .sorted((c1, c2) -> {
                    // Sort by rating and recent enrollments
                    int ratingComparison = c2.getRating().compareTo(c1.getRating());
                    if (ratingComparison != 0) {
                        return ratingComparison;
                    }

                    // If ratings are equal, sort by enrollment count
                    long c1Enrollments = enrollmentRepository.findByCourseId(c1.getId()).size();
                    long c2Enrollments = enrollmentRepository.findByCourseId(c2.getId()).size();
                    return Long.compare(c2Enrollments, c1Enrollments);
                })
                .collect(Collectors.toList());

        // Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sortedCourses.size());

        if (start >= sortedCourses.size()) {
            return Page.empty(pageable);
        }

        List<CourseDTO> pageContent = sortedCourses.subList(start, end).stream()
                .map(CourseDTO::new)
                .collect(Collectors.toList());

        return new org.springframework.data.domain.PageImpl<>(
                pageContent, pageable, sortedCourses.size());
    }

    /**
     * Get courses similar to a specific course
     */
    public Page<CourseDTO> getSimilarCourses(Integer courseId, Pageable pageable) {
        courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Get categories of the target course
        List<CourseCategory> targetCourseCategories = courseCategoryRepository.findByCourseId(courseId);
        Set<Integer> targetCategoryIds = targetCourseCategories.stream()
                .map(courseCategory -> courseCategory.getCategory().getId())
                .collect(Collectors.toSet());

        // Find courses with similar categories
        List<Course> allCourses = courseRepository.findByIsPublishedTrue();
        List<CourseScore> similarCourses = allCourses.stream()
                .filter(course -> !course.getId().equals(courseId))
                .map(course -> {
                    List<CourseCategory> courseCategories = courseCategoryRepository.findByCourseId(course.getId());
                    Set<Integer> courseCategoryIds = courseCategories.stream()
                            .map(courseCategory -> courseCategory.getCategory().getId())
                            .collect(Collectors.toSet());

                    // Calculate similarity score
                    double similarityScore = calculateCategorySimilarity(targetCategoryIds, courseCategoryIds);
                    return new CourseScore(course, similarityScore);
                })
                .sorted(Comparator.comparing(CourseScore::getScore).reversed())
                .collect(Collectors.toList());

        List<Course> sortedCourses = similarCourses.stream()
                .map(CourseScore::getCourse)
                .collect(Collectors.toList());

        // Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sortedCourses.size());

        if (start >= sortedCourses.size()) {
            return Page.empty(pageable);
        }

        List<CourseDTO> pageContent = sortedCourses.subList(start, end).stream()
                .map(CourseDTO::new)
                .collect(Collectors.toList());

        return new org.springframework.data.domain.PageImpl<>(
                pageContent, pageable, sortedCourses.size());
    }

    /**
     * Calculate similarity between two sets of category IDs
     */
    private double calculateCategorySimilarity(Set<Integer> categories1, Set<Integer> categories2) {
        if (categories1.isEmpty() || categories2.isEmpty()) {
            return 0.0;
        }

        Set<Integer> intersection = new HashSet<>(categories1);
        intersection.retainAll(categories2);

        Set<Integer> union = new HashSet<>(categories1);
        union.addAll(categories2);

        return (double) intersection.size() / union.size();
    }

    /**
     * Inner class to hold course and its score
     */
    private static class CourseScore {
        private final Course course;
        private final double score;

        public CourseScore(Course course, double score) {
            this.course = course;
            this.score = score;
        }

        public Course getCourse() {
            return course;
        }

        public double getScore() {
            return score;
        }
    }
}
