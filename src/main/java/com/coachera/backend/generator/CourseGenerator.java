package com.coachera.backend.generator;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.instancio.Instancio;
import org.instancio.Select;

import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Organization;

public class CourseGenerator {
     public static List<Course> fromOrg(List<Organization> organizations) {
        return organizations.stream()
            .map(org -> {
                try {
                    Course course = Instancio.of(Course.class)
                        .ignore(Select.field(Course::getId))
                        .supply(Select.field(Course::getOrg), () -> org)
                        .supply(Select.field(Course::getTitle), () -> "Course " + BigDecimal.valueOf(Math.random() * 50))
                        .supply(Select.field(Course::getDescription),() -> "Description...")
                        .supply(Select.field(Course::getDurationHours), () -> getRandomDuration())
                        .supply(Select.field(Course::getPrice), () -> (BigDecimal.valueOf(Math.random() * 50)))
                        .supply(Select.field(Course::getRating), () -> (BigDecimal.valueOf(Math.random())))
                        .ignore(Select.field(Course::getCategories))
                        .ignore(Select.field(Course::getLearningPaths))
                        .create();
                    
                    if (course == null) {
                        throw new IllegalStateException("Instancio returned null Course");
                    }
                    return course;
                } catch (Exception e) {
                    throw new IllegalStateException("Failed to create Course for organization " + org.getId(), e);
                }
            })
            .collect(Collectors.toList());
    }

    private static String getRandomDuration() {
        String[] durations = {"20 hours", "40 hours", "60 hours", "80 hours", "100 hours"};
        return durations[(int)(Math.random() * durations.length)];
    }
}
