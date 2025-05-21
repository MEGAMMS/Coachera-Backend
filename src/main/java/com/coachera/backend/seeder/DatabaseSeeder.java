package com.coachera.backend.seeder;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coachera.backend.entity.Category;
import com.coachera.backend.entity.Certificate;
import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Enrollment;
import com.coachera.backend.entity.Instructor;
import com.coachera.backend.entity.Material;
import com.coachera.backend.entity.Organization;
import com.coachera.backend.entity.Question;
import com.coachera.backend.entity.Quiz;
import com.coachera.backend.entity.Section;
import com.coachera.backend.entity.Student;
import com.coachera.backend.entity.User;
import com.coachera.backend.entity.Week;

import com.coachera.backend.generator.CategoryGenerator;
import com.coachera.backend.generator.CertificateGenerator;
import com.coachera.backend.generator.CourseGenerator;
import com.coachera.backend.generator.EnrollmentGenerator;
import com.coachera.backend.generator.InstructorGenerator;
import com.coachera.backend.generator.MaterialGenerator;
import com.coachera.backend.generator.OrganizationGenerator;
import com.coachera.backend.generator.QuestionGenerator;
import com.coachera.backend.generator.QuizGenerator;
import com.coachera.backend.generator.SectionGenerator;
import com.coachera.backend.generator.StudentGenerator;
import com.coachera.backend.generator.UserGenerator;
import com.coachera.backend.generator.WeekGenerator;

import com.coachera.backend.repository.CategoryRepository;
import com.coachera.backend.repository.CertificateRepository;
import com.coachera.backend.repository.CourseRepository;
import com.coachera.backend.repository.EnrollmentRepository;
import com.coachera.backend.repository.InstructorRepository;
import com.coachera.backend.repository.MaterialRepository;
import com.coachera.backend.repository.OrganizationRepository;
import com.coachera.backend.repository.QuestionRepository;
import com.coachera.backend.repository.QuizRepository;
import com.coachera.backend.repository.SectionRepository;
import com.coachera.backend.repository.StudentRepository;
import com.coachera.backend.repository.UserRepository;
import com.coachera.backend.repository.WeekRepository;

@Component
public class DatabaseSeeder {

    private final UserRepository userRepo;
    private final StudentRepository studentRepo;
    private final InstructorRepository instructorRepo;
    private final OrganizationRepository orgRepo;
    private final CourseRepository courseRepo;
    private final CategoryRepository categoryRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final CertificateRepository certificateRepo;
    private final WeekRepository weekRepo;
    private final SectionRepository sectionRepo;
    private final MaterialRepository materialRepo;
    private final QuizRepository quizRepo;
    private final QuestionRepository questionRepo;

    public DatabaseSeeder(
            UserRepository userRepo,
            StudentRepository studentRepo,
            InstructorRepository instructorRepo,
            OrganizationRepository orgRepo,
            CourseRepository courseRepo,
            CategoryRepository categoryRepo,
            EnrollmentRepository enrollmentRepo,
            CertificateRepository certificateRepo,
            WeekRepository weekRepo,
            SectionRepository sectionRepo,
            MaterialRepository materialRepo,
            QuizRepository quizRepo,
            QuestionRepository questionRepo) {
        this.userRepo = userRepo;
        this.studentRepo = studentRepo;
        this.instructorRepo = instructorRepo;
        this.orgRepo = orgRepo;
        this.courseRepo = courseRepo;
        this.categoryRepo = categoryRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.certificateRepo = certificateRepo;
        this.weekRepo = weekRepo;
        this.sectionRepo = sectionRepo;
        this.materialRepo = materialRepo;
        this.quizRepo = quizRepo;
        this.questionRepo = questionRepo;
    }

    @Transactional
    public void run() {
        // Generate and assign roles before saving
        List<User> users = UserGenerator.generate(15);

        // Assign roles
        for (int i = 0; i < users.size(); i++) {
            if (i < 4) {
                users.get(i).setRole("STUDENT");
            } else if (i < 8) {
                users.get(i).setRole("INSTRUCTOR");
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

        List<User> instructorUsers = users.stream()
                .filter(user -> "INSTRUCTOR".equalsIgnoreCase(user.getRole()))
                .collect(Collectors.toList());

        List<User> orgUsers = users.stream()
                .filter(user -> "ORGANIZATION".equalsIgnoreCase(user.getRole()))
                .collect(Collectors.toList());

        // Seed students
        List<Student> students = StudentGenerator.fromUsers(studentUsers);
        studentRepo.saveAll(students);

        // Seed admin
        List<Instructor> admins = InstructorGenerator.fromUsers(instructorUsers);
        instructorRepo.saveAll(admins);

        // Seed organizations
        List<Organization> orgs = OrganizationGenerator.fromUsers(orgUsers);
        orgRepo.saveAll(orgs);

        // Seed courses per org
        List<Course> courses = CourseGenerator.fromOrg(orgs);
        courseRepo.saveAll(courses);

        // Seed categories
        List<Category> categories = CategoryGenerator.fromCourses(courses, List.of("AI", "Web", "Business", "Data"));
        categoryRepo.saveAll(categories);

        // Seed enrollments for first few courses
        List<Enrollment> enrollments = EnrollmentGenerator.forStudentsAndCourses(students, courses.subList(0, 5));
        enrollmentRepo.saveAll(enrollments);

        // Seed certificates (optional)
        List<Certificate> certificates = CertificateGenerator.fromCourses(courses);
        certificateRepo.saveAll(certificates);

        // Seed Weeks
        List<Week> weeks = WeekGenerator.fromCourses(courses);
        weekRepo.saveAll(weeks);

        // Seed Sections
        List<Section> sections = SectionGenerator.fromWeeks(weeks);
        sectionRepo.saveAll(sections);

        // Seed Materials
        List<Material> materials = MaterialGenerator.fromSections(sections);
        materialRepo.saveAll(materials);

        // Seed Quizzes
        List<Quiz> quizzes = QuizGenerator.fromMaterials(materials);
        quizRepo.saveAll(quizzes);

        // Seed questions
        List<Question> questions =QuestionGenerator.fromQuizzes(quizzes);
        questionRepo.saveAll(questions);
    }

    @Transactional
    public void clean() {
        questionRepo.deleteAll();
        quizRepo.deleteAll();
        materialRepo.deleteAll();
        sectionRepo.deleteAll();
        weekRepo.deleteAll();
        certificateRepo.deleteAll();
        enrollmentRepo.deleteAll();
        courseRepo.deleteAll();
        categoryRepo.deleteAll();
        orgRepo.deleteAll();
        instructorRepo.deleteAll();
        studentRepo.deleteAll();
        userRepo.deleteAll();
    }
}
