package com.coachera.backend.seeder;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coachera.backend.entity.Admin;
import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Organization;
import com.coachera.backend.entity.Student;
import com.coachera.backend.entity.User;
import com.coachera.backend.generator.AdminGenerator;
import com.coachera.backend.generator.CourseGenerator;
import com.coachera.backend.generator.OrganizationGenerator;
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
@Transactional
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
        // Seed students
        List<Student> students = StudentGenerator.fromUsers(users.subList(0, 4));
        studentRepo.saveAll(students);
        // Seed admin
        List<Admin> admin = AdminGenerator.fromUsers(users.subList(4, 5));
        adminRepo.saveAll(admin);
        // Seed orgs
        List<Organization> orgs = OrganizationGenerator.fromUsers(users.subList(5, 10));
        orgRepo.saveAll(orgs);
        // Seed courses per org
        // List<Course> courses = CourseGenerator.forOrgs(orgs, 3);
        // courseRepo.saveAll(courses);


    }
    
}
