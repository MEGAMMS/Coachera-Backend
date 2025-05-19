package com.coachera.backend.seeder;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coachera.backend.entity.Admin;
import com.coachera.backend.entity.Category;
import com.coachera.backend.entity.Certificate;
import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Enrollment;
import com.coachera.backend.entity.Organization;
import com.coachera.backend.entity.Student;
import com.coachera.backend.entity.User;
import com.coachera.backend.generator.AdminGenerator;
import com.coachera.backend.generator.CategoryGenerator;
// import com.coachera.backend.generator.CertificateGenerator;
import com.coachera.backend.generator.CourseGenerator;
import com.coachera.backend.generator.EnrollmentGenerator;
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
public class DatabaseSeeder {

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

    @Transactional
    public void run() {
        // Generate and assign roles before saving
        List<User> users = UserGenerator.generate(10);

        // Assign roles
        for (int i = 0; i < users.size(); i++) {
            if (i < 4) {
                users.get(i).setRole("STUDENT");
            } else if (i == 4) {
                users.get(i).setRole("ADMIN");
            } else {
                users.get(i).setRole("ORGANIZATION");
            }
        }

        userRepo.saveAll(users);
        userRepo.flush();

        // Filter users by role
        List<User> studentUsers = users.stream()
            .filter(user -> "STUDENT".equalsIgnoreCase(user.getRole()))
            .collect(Collectors.toList());

        List<User> adminUsers = users.stream()
            .filter(user -> "ADMIN".equalsIgnoreCase(user.getRole()))
            .collect(Collectors.toList());

        List<User> orgUsers = users.stream()
            .filter(user -> "ORGANIZATION".equalsIgnoreCase(user.getRole()))
            .collect(Collectors.toList());

        // Seed students
        List<Student> students = StudentGenerator.fromUsers(studentUsers);
        studentRepo.saveAll(students);

        // Seed admin
        List<Admin> admins = AdminGenerator.fromUsers(adminUsers);
        adminRepo.saveAll(admins);

        // Seed organizations
        List<Organization> orgs = OrganizationGenerator.fromUsers(orgUsers);
        orgRepo.saveAll(orgs);

        // // Seed courses per org
        // List<Course> courses = CourseGenerator.forOrgs(orgs, 3);
        // courseRepo.saveAll(courses);

        // // Seed categories
        // List<Category> categories = CategoryGenerator.fromNames(List.of("AI", "Web", "Business", "Data"));
        // categoryRepo.saveAll(categories);

        // // Seed enrollments for first few courses
        // List<Enrollment> enrollments = EnrollmentGenerator.forStudentsAndCourses(students, courses.subList(0, 5));
        // enrollmentRepo.saveAll(enrollments);

        // Seed certificates (optional)
        // List<Certificate> certificates = CertificateGenerator.forStudentsAndCourses(students, courses.subList(0, 3));
        // certificateRepo.saveAll(certificates);
    }

    @Transactional
    public void clean() {
        certificateRepo.deleteAll();
        enrollmentRepo.deleteAll();
        courseRepo.deleteAll();
        categoryRepo.deleteAll();
        orgRepo.deleteAll();
        adminRepo.deleteAll();
        studentRepo.deleteAll();
        userRepo.deleteAll();
    }
}
