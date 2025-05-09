package com.coachera.backend.generator;

import java.util.List;
import java.util.stream.Collectors;

import org.instancio.Instancio;
import org.instancio.Select;

import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Enrollment;
import com.coachera.backend.entity.Student;

public class EnrollmentGenerator {
     public static List<Enrollment> forStudentsAndCourses(List<Student> students, List<Course> courses) {
        return students.stream().flatMap(student ->
            courses.stream().map(course ->
                Instancio.of(Enrollment.class)
                    .ignore(Select.field(Enrollment::getId))
                    .supply(Select.field(Enrollment::getStudent), () -> student)
                    .supply(Select.field(Enrollment::getCourse), () -> course)
                    .supply(Select.field(Enrollment::getProgress), () -> "0%")
                    .create()
            )
        ).collect(Collectors.toList());
    }
}
