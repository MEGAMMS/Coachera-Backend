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

    // Basic generator with default values
    public static List<Certificate> fromCourses(List<Course> courses) {
        return fromCourses(courses, 
            LocalDate.now(), 
            "https://coachera.com/certificates/", 
            "CERT-", 
            2);
    }

    // Configurable generator
    public static List<Certificate> fromCourses(
            List<Course> courses,
            LocalDate baseIssueDate,
            String urlPrefix,
            String certNumberPrefix,
            Integer defaultValidityYears) {
        
        return courses.stream()
            .map(course -> {
                try {
                    if (course == null) {
                        throw new IllegalArgumentException("Course cannot be null");
                    }

                    Certificate certificate = Instancio.of(Certificate.class)
                        .ignore(Select.field(Certificate::getId))
                        .supply(Select.field(Certificate::getCourse), () -> course)
                        .supply(Select.field(Certificate::getIssuedAt), () -> baseIssueDate)
                        .supply(Select.field(Certificate::getCertificateUrl), () -> 
                            urlPrefix + course.getId() + "-" + System.currentTimeMillis())
                        .supply(Select.field(Certificate::getCertificatePreviewUrl),()->  urlPrefix + course.getId() + "-" + System.currentTimeMillis()+"/preview.png")
                        .supply(Select.field(Certificate::getCertificateNumber), () -> 
                            certNumberPrefix + course.getId() + "-" + (int)(Math.random() * 10000))
                        .supply(Select.field(Certificate::getValidityYears), () -> defaultValidityYears)
                        .ignore(Select.field(Certificate::getStudents))
                        .create();
                    
                    if (certificate == null) {
                        throw new IllegalStateException("Instancio returned null Certificate");
                    }
                    return certificate;
                } catch (Exception e) {
                    throw new IllegalStateException(
                        "Failed to create Certificate for course " + course.getId(), e);
                }
            })
            .collect(Collectors.toList());
    }

    // Generator with students included
    public static List<Certificate> withStudents(
            List<Course> courses, 
            List<Student> students,
            int studentsPerCertificate) {
        
        List<Certificate> certificates = fromCourses(courses);
        
        // Distribute students evenly across certificates
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            Certificate certificate = certificates.get(i % certificates.size());
            certificate.addStudent(student);
        }
        
        return certificates;
    }
}