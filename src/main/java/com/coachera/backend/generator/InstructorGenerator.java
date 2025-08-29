package com.coachera.backend.generator;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.instancio.Instancio;
import org.instancio.Select;

import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Instructor;
import com.coachera.backend.entity.User;

public class InstructorGenerator {

    private static final Random RANDOM = new Random();
    
    public static List<Instructor> fromUsers(List<User> users) {
        return users.stream()
            .map(user -> {
                try {
                    Instructor instructor = Instancio.of(Instructor.class)
                        .ignore(Select.field(Instructor::getId))
                        .supply(Select.field(Instructor::getUser), () -> user)
                        .supply(Select.field(Instructor::getBio), () -> "Experienced instructor with a passion for teaching.")
                        .supply(Select.field(Instructor::getFullname), () -> "Muhannad Wahbeh")
                        .ignore(Select.field(Instructor::getCourses))
                        .create();
                    
                    if (instructor == null) {
                        throw new IllegalStateException("Instancio returned null Instructor");
                    }
                    return instructor;
                } catch (Exception e) {
                    throw new IllegalStateException("Failed to create Instructor for user " + user.getId(), e);
                }
            })
            .collect(Collectors.toList());
    }

      /**
     * Assigns courses to instructors randomly.
     * Each instructor will get between 1 and maxCoursesPerInstructor courses.
     */
    public static void assignCourses(List<Instructor> instructors, List<Course> courses, int maxCoursesPerInstructor) {
        if (courses == null || courses.isEmpty()) {
            return; // no courses to assign
        }

        for (Instructor instructor : instructors) {
            int numCourses = 1 + RANDOM.nextInt(Math.max(1, maxCoursesPerInstructor));
            for (int i = 0; i < numCourses; i++) {
                Course course = courses.get(RANDOM.nextInt(courses.size()));
                instructor.addCourse(course);
                // course.addInstructor(instructor); // maintain bidirectional relationship
            }
        }
    }
}