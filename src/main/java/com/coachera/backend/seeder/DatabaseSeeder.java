package com.coachera.backend.seeder;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.coachera.backend.entity.Student;
import com.coachera.backend.entity.User;
import com.coachera.backend.generator.StudentGenerator;
import com.coachera.backend.generator.UserGenerator;
import com.coachera.backend.repository.AdminRepository;
import com.coachera.backend.repository.CategoryRepository;
import com.coachera.backend.repository.CertificateRepository;
import com.coachera.backend.repository.CourseRepository;
import com.coachera.backend.repository.EnrollmentRepository;
import com.coachera.backend.repository.OrganizationRepository;
import com.coachera.backend.repository.StudentRepository;
import com.coachera.backend.repository.UserRepository;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final StudentRepository studentRepo;
    private final AdminRepository adminRepo;
    private final OrganizationRepository orgRepo;
    private final CourseRepository courseRepo;
    private final CategoryRepository categoryRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final CertificateRepository certificateRepo;

    public DatabaseSeeder(
        UserRepository userRepo,
        StudentRepository studentRepo,
        AdminRepository adminRepo,
        OrganizationRepository orgRepo,
        CourseRepository courseRepo,
        CategoryRepository categoryRepo,
        EnrollmentRepository enrollmentRepo,
        CertificateRepository certificateRepo
    ) {
        this.userRepo = userRepo;
        this.studentRepo = studentRepo;
        this.adminRepo = adminRepo;
        this.orgRepo = orgRepo;
        this.courseRepo = courseRepo;
        this.categoryRepo = categoryRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.certificateRepo = certificateRepo;
    }

    @Override
    public void run(String... args) throws Exception {

        // Seed users
        List<User> users = UserGenerator.generate(10);
        userRepo.saveAll(users);

        List<Student> students = StudentGenerator.fromUsers(users.subList(0, 4));
        studentRepo.saveAll(students);
    }
    
}
