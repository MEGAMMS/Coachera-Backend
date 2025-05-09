package com.coachera.backend.generator;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.instancio.Instancio;
import org.instancio.Select;

import com.coachera.backend.entity.Certificate;
import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Student;

public class CertificateGenerator {
    public static List<Certificate> forStudentsAndCourses(List<Student> students, List<Course> courses) {
        return students.stream().flatMap(student ->
            courses.stream().map(course ->
                Instancio.of(Certificate.class)
                    .ignore(Select.field(Certificate::getId))
                    .supply(Select.field(Certificate::getStudent), () -> student)
                    .supply(Select.field(Certificate::getCourse), () -> course)
                    .supply(Select.field(Certificate::getIssuedAt), () -> LocalDate.now())
                    .supply(Select.field(Certificate::getCertificateUrl), () -> "http://certs.fake/certificate.pdf")
                    .create()
            )
        ).collect(Collectors.toList());
    }
}
