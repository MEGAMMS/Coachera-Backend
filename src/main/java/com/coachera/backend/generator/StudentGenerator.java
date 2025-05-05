package com.coachera.backend.generator;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.instancio.Instancio;
import org.instancio.Select;

import com.coachera.backend.entity.Student;
import com.coachera.backend.entity.User;

public class StudentGenerator {
    public static List<Student> fromUsers(List<User> users) {
        return users.stream().map(user ->
            Instancio.of(Student.class)
                .ignore(Select.field(Student::getId)) // ← prevent random ID generation
                .supply(Select.field(Student::getUser), () -> user)
                .supply(Select.field(Student::getGender), () -> "male")
                .supply(Select.field(Student::getEducation), () -> "Bachelor")
                .supply(Select.field(Student::getBirthDate), () -> LocalDate.of(2000, 1, 1))
                .create()
        ).collect(Collectors.toList());
    }
}
