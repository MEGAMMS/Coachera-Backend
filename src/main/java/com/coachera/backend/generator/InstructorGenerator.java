package com.coachera.backend.generator;

import java.util.List;
import java.util.stream.Collectors;

import org.instancio.Instancio;
import org.instancio.Select;

import com.coachera.backend.entity.Instructor;
import com.coachera.backend.entity.User;

public class InstructorGenerator {

    public static List<Instructor> fromUsers(List<User> users) {
        return users.stream()
            .map(user -> {
                try {
                    Instructor instructor = Instancio.of(Instructor.class)
                        .ignore(Select.field(Instructor::getId))
                        .supply(Select.field(Instructor::getUser), () -> user)
                        .supply(Select.field(Instructor::getBio), () -> "Experienced instructor with a passion for teaching.")
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
}