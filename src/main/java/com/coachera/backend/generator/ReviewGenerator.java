package com.coachera.backend.generator;

import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Review;
import com.coachera.backend.entity.Student;
import org.instancio.Instancio;
import org.instancio.Select;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ReviewGenerator {

    private static final Random random = new Random();
    private static final String[] POSITIVE_COMMENTS = {
        "Great course! Learned a lot.",
        "Excellent content and instructor.",
        "Very helpful for my career growth.",
        "Would definitely recommend to others.",
        "The best course I've taken on this subject."
    };

    private static final String[] NEUTRAL_COMMENTS = {
        "Decent course, but could be improved.",
        "Some good content, but needs updates.",
        "Average experience overall.",
        "Met my basic expectations.",
        "Not bad, but not exceptional either."
    };

    private static final String[] NEGATIVE_COMMENTS = {
        "Disappointed with the course quality.",
        "Content was outdated and not useful.",
        "Would not recommend to others.",
        "Expected more for the price.",
        "Poor organization and delivery."
    };

    public static List<Review> generateReviews(List<Course> courses, List<Student> students) {
        if (courses == null || courses.isEmpty() || students == null || students.isEmpty()) {
            throw new IllegalArgumentException("Courses and students lists cannot be null or empty");
        }

        return courses.stream()
            .flatMap(course -> {
                // Ensure course and students are persisted
                if (course.getId() == null) {
                    throw new IllegalStateException("Course must be persisted first");
                }

                int reviewCount = random.nextInt(5) + 1; // 1-5 reviews per course
                
                return Instancio.ofList(Review.class)
                    .size(reviewCount)
                    .ignore(Select.field(Review::getId))
                    .supply(Select.field(Review::getCourse), () -> course)
                    .supply(Select.field(Review::getStudent), () -> 
                        students.get(random.nextInt(students.size())))
                    .supply(Select.field(Review::getRating), () -> 
                        random.nextInt(5) + 1) // 1-5 rating
                    .supply(Select.field(Review::getComment), () -> 
                        generateCommentBasedOnRating())
                    .create()
                    .stream();
            })
            .collect(Collectors.toList());
    }

    private static String generateCommentBasedOnRating() {
        int rating = random.nextInt(5) + 1;
        if (rating >= 4) {
            return POSITIVE_COMMENTS[random.nextInt(POSITIVE_COMMENTS.length)];
        } else if (rating == 3) {
            return NEUTRAL_COMMENTS[random.nextInt(NEUTRAL_COMMENTS.length)];
        } else {
            return NEGATIVE_COMMENTS[random.nextInt(NEGATIVE_COMMENTS.length)];
        }
    }
}