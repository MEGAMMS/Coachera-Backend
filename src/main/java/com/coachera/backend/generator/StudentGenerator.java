package com.coachera.backend.generator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.instancio.Instancio;
import org.instancio.Select;

import com.coachera.backend.entity.Student;
import com.coachera.backend.entity.User;

public class StudentGenerator {

    public static List<Student> fromUsers(List<User> users) {
        return users.stream()
            .map(user -> {
                try {
                    Student student = Instancio.of(Student.class)
                        .ignore(Select.field(Student::getId))
                        .supply(Select.field(Student::getUser), () -> user)
                        .supply(Select.field(Student::getFirstName), () -> "Student")
                        .supply(Select.field(Student::getLastName), () -> "User" + user.getId())
                        .supply(Select.field(Student::getBirthDate), () -> LocalDate.now().minusYears(20))
                        .supply(Select.field(Student::getGender), () -> "male")
                        .supply(Select.field(Student::getEducation), () -> "Bachelor")
                        .supply(Select.field(Student::getWallet), () -> BigDecimal.ZERO)
                        .ignore(Select.field(Student::getStudentCertificates))
                        .ignore(Select.field(Student::getStudentSkills))
                        .create();
                    
                    if (student == null) {
                        throw new IllegalStateException("Instancio returned null Student");
                    }
                    return student;
                } catch (Exception e) {
                    throw new IllegalStateException("Failed to create Student for user " + user.getId(), e);
                }
            })
            .collect(Collectors.toList());
    }
}
