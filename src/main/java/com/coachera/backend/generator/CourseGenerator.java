package com.coachera.backend.generator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.instancio.Instancio;
import org.instancio.Select;

import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Organization;

public class CourseGenerator {
    public static List<Course> fromOrg(List<Organization> organizations) {

        if (organizations == null || organizations.isEmpty()) {
            throw new IllegalArgumentException("Orgs list cannot be null or empty");
        }

        Random random = new Random();

        return organizations.stream()
                .flatMap(org -> {
                    // Ensure org is managed/persisted
                    if (org.getId() == null) {
                        throw new IllegalStateException("Organization must be persisted first (id cannot be null)");
                    }
                    AtomicInteger courseOrderCounter = new AtomicInteger(0);
                    int courseCount = random.nextInt(8) + 3; // 3-10 courses per org
                    List<Course> courses = new ArrayList<>();

                    for (int i = 0; i < courseCount; i++) {
                        try {
                            Course course = Instancio.of(Course.class)
                                    .ignore(Select.field(Course::getId))
                                    .supply(Select.field(Course::getOrg), () -> org)
                                    .supply(Select.field(Course::getTitle),
                                            () -> "Course" + courseOrderCounter.incrementAndGet())
                                    .supply(Select.field(Course::getDescription),
                                            () -> "Comprehensive course on " + org.getOrgName() + " topics")
                                    .supply(Select.field(Course::getDurationHours),
                                            () -> getRandomDuration())
                                    .supply(Select.field(Course::getPrice),
                                            () -> BigDecimal.valueOf(10 + random.nextDouble() * 90)
                                                    .setScale(2, RoundingMode.HALF_UP))
                                    .supply(Select.field(Course::getRating),
                                            () -> BigDecimal.valueOf(3 + random.nextDouble() * 2)
                                                    .setScale(1, RoundingMode.HALF_UP))
                                    .ignore(Select.field(Course::getCategories))
                                    .ignore(Select.field(Course::getLearningPaths))
                                    .ignore(Select.field(Course::getImage))
                                    .ignore(Select.field(Course::getModules))
                                    .create();

                            if (course == null) {
                                throw new IllegalStateException("Instancio returned null Course");
                            }

                            courses.add(course);
                        } catch (Exception e) {
                            throw new IllegalStateException(
                                    "Failed to create Course for organization " + org.getId(), e);
                        }
                    }
                    return courses.stream();
                })
                .collect(Collectors.toList());
    }

    private static String getRandomDuration() {
        String[] durations = { "20 hours", "40 hours", "60 hours", "80 hours", "100 hours" };
        return durations[(int) (Math.random() * durations.length)];
    }
}
