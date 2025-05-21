package com.coachera.backend.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.instancio.Instancio;
import org.instancio.Select;

import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Week;

public class WeekGenerator {

    public static List<Week> fromCourses(List<Course> courses) {
        AtomicInteger orderCounter = new AtomicInteger(0);
        Random random = new Random();

        return courses.stream()
                .flatMap(course -> {
                    int weeksCount = random.nextInt(10) + 1; // Random number between 1-10
                    List<Week> weeks = new ArrayList<>();

                    for (int i = 0; i < weeksCount; i++) {
                        try {
                            Week week = Instancio.of(Week.class)
                                    .ignore(Select.field(Week::getId))
                                    .supply(Select.field(Week::getCourse), () -> course)
                                    .supply(Select.field(Week::getOrderIndex), orderCounter::getAndIncrement)
                                    .create();

                            if (week == null) {
                                throw new IllegalStateException("Instancio returned null Week");
                            }
                            weeks.add(week);
                        } catch (Exception e) {
                            throw new IllegalStateException("Failed to create Week for course " + course.getId(), e);
                        }
                    }
                    return weeks.stream();
                })
                .collect(Collectors.toList());
    }

    public static List<Week> fromCourses(List<Course> courses, int startingOrderIndex) {
        AtomicInteger orderCounter = new AtomicInteger(startingOrderIndex);

        return courses.stream()
                .map(course -> {
                    try {
                        Week week = Instancio.of(Week.class)
                                .ignore(Select.field(Week::getId))
                                .supply(Select.field(Week::getCourse), () -> course)
                                .supply(Select.field(Week::getOrderIndex), orderCounter::getAndIncrement)
                                .create();

                        if (week == null) {
                            throw new IllegalStateException("Instancio returned null Week");
                        }
                        return week;
                    } catch (Exception e) {
                        throw new IllegalStateException("Failed to create Week for course " + course.getId(), e);
                    }
                })
                .collect(Collectors.toList());
    }
}