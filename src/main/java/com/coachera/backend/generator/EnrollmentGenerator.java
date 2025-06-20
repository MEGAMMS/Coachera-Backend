package com.coachera.backend.generator;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.instancio.Instancio;
import org.instancio.Select;

import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Enrollment;
import com.coachera.backend.entity.Student;

public class EnrollmentGenerator {
    private static final Random random = new Random();

    public static List<Enrollment> forStudentsAndCourses(List<Student> students, List<Course> courses) {
        return students.stream()
            .flatMap(student -> {
                // Determine random number of courses for this student (at least 1)
                int coursesToEnroll = random.nextInt(courses.size()) + 1;
                
                // Select random subset of courses
                List<Course> randomCourses = random.ints(0, courses.size())
                    .distinct()
                    .limit(coursesToEnroll)
                    .mapToObj(courses::get)
                    .collect(Collectors.toList());
                
                return randomCourses.stream().map(course ->
                    Instancio.of(Enrollment.class)
                        .ignore(Select.field(Enrollment::getId))
                        .supply(Select.field(Enrollment::getStudent), () -> student)
                        .supply(Select.field(Enrollment::getCourse), () -> course)
                        .ignore(Select.field(Enrollment::getCourseCompletion))
                        .ignore(Select.field(Enrollment::getMaterialCompletions))
                        .create()
                );
            })
            .collect(Collectors.toList());
    }
}