package com.coachera.backend.seeder;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coachera.backend.entity.Category;
import com.coachera.backend.entity.Certificate;
import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.CourseCompletion;
import com.coachera.backend.entity.Enrollment;
import com.coachera.backend.entity.Instructor;
import com.coachera.backend.entity.LearningPath;
import com.coachera.backend.entity.Material;
import com.coachera.backend.entity.MaterialCompletion;
import com.coachera.backend.entity.Organization;
import com.coachera.backend.entity.Question;
import com.coachera.backend.entity.Quiz;
import com.coachera.backend.entity.Review;
import com.coachera.backend.entity.Section;
import com.coachera.backend.entity.Skill;
import com.coachera.backend.entity.Student;
import com.coachera.backend.entity.User;
import com.coachera.backend.entity.Module;

import com.coachera.backend.generator.CategoryGenerator;
import com.coachera.backend.generator.CertificateGenerator;
import com.coachera.backend.generator.CourseCompletionGenerator;
import com.coachera.backend.generator.CourseGenerator;
import com.coachera.backend.generator.EnrollmentGenerator;
import com.coachera.backend.generator.InstructorGenerator;
import com.coachera.backend.generator.LearningPathGenerator;
import com.coachera.backend.generator.MaterialCompletionGenerator;
import com.coachera.backend.generator.MaterialGenerator;
import com.coachera.backend.generator.OrganizationGenerator;
import com.coachera.backend.generator.QuestionGenerator;
import com.coachera.backend.generator.QuizGenerator;
import com.coachera.backend.generator.ReviewGenerator;
import com.coachera.backend.generator.SectionGenerator;
import com.coachera.backend.generator.SkillGenerator;
import com.coachera.backend.generator.StudentGenerator;
import com.coachera.backend.generator.UserGenerator;
import com.coachera.backend.generator.ModuleGenerator;

import com.coachera.backend.repository.CategoryRepository;
import com.coachera.backend.repository.CertificateRepository;
import com.coachera.backend.repository.CourseCompletionRepository;
import com.coachera.backend.repository.CourseRepository;
import com.coachera.backend.repository.EnrollmentRepository;
import com.coachera.backend.repository.InstructorRepository;
import com.coachera.backend.repository.LearningPathRepository;
import com.coachera.backend.repository.MaterialCompletionRepository;
import com.coachera.backend.repository.MaterialRepository;
import com.coachera.backend.repository.OrganizationRepository;
import com.coachera.backend.repository.QuestionRepository;
import com.coachera.backend.repository.QuizRepository;
import com.coachera.backend.repository.ReviewRepository;
import com.coachera.backend.repository.SectionRepository;
import com.coachera.backend.repository.SkillRepository;
import com.coachera.backend.repository.StudentRepository;
import com.coachera.backend.repository.UserRepository;
import com.coachera.backend.repository.ModuleRepository;

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
    private final ModuleRepository moduleRepo;
    private final SectionRepository sectionRepo;
    private final MaterialRepository materialRepo;
    private final QuizRepository quizRepo;
    private final QuestionRepository questionRepo;
    private final ReviewRepository reviewRepo;
    private final LearningPathRepository learningPathRepo;
    private final SkillRepository skillRepo;
    private final UserGenerator userGenerator;
    private final MaterialCompletionRepository materialCompletionRepo;
    private final CourseCompletionRepository courseCompletionRepo;

    public DatabaseSeeder(
            UserRepository userRepo,
            StudentRepository studentRepo,
            InstructorRepository instructorRepo,
            OrganizationRepository orgRepo,
            CourseRepository courseRepo,
            CategoryRepository categoryRepo,
            EnrollmentRepository enrollmentRepo,
            CertificateRepository certificateRepo,
            ModuleRepository moduleRepo,
            SectionRepository sectionRepo,
            MaterialRepository materialRepo,
            QuizRepository quizRepo,
            QuestionRepository questionRepo,
            ReviewRepository reviewRepo,
            LearningPathRepository learningPathRepo,
            SkillRepository skillRepo,
            UserGenerator userGenerator,
            MaterialCompletionRepository materialCompletionRepo,
            CourseCompletionRepository courseCompletionRepo) {
        this.userGenerator = userGenerator;
        this.userRepo = userRepo;
        this.studentRepo = studentRepo;
        this.instructorRepo = instructorRepo;
        this.orgRepo = orgRepo;
        this.courseRepo = courseRepo;
        this.categoryRepo = categoryRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.certificateRepo = certificateRepo;
        this.moduleRepo = moduleRepo;
        this.sectionRepo = sectionRepo;
        this.materialRepo = materialRepo;
        this.quizRepo = quizRepo;
        this.questionRepo = questionRepo;
        this.reviewRepo = reviewRepo;
        this.learningPathRepo = learningPathRepo;
        this.skillRepo = skillRepo;
        this.materialCompletionRepo = materialCompletionRepo;
        this.courseCompletionRepo = courseCompletionRepo;
    }

    @Transactional
    public void run() {
        // Generate and assign roles before saving
        List<User> users = userGenerator.generate(15);

        users.get(0).setRole("STUDENT");
        users.get(0).setEmail("student@gmail.com");
        users.get(1).setRole("INSTRUCTOR");
        users.get(1).setEmail("instructer@gmail.com");
        users.get(2).setRole("ORGANIZATION");
        users.get(2).setEmail("organization@gmail.com");
        // Assign roles
        for (int i = 3; i < users.size(); i++) {
            // users.get(i).setProfileImage(images.get(i));
            if (i < 5) {
                users.get(i).setRole("STUDENT");
                users.get(i).setEmail("student" + i + "@gmail.com");
            } else if (i < 10) {
                users.get(i).setRole("INSTRUCTOR");
                users.get(i).setEmail("instructer" + i + "@gmail.com");
            } else {
                users.get(i).setRole("ORGANIZATION");
                users.get(i).setEmail("organization" + i + "@gmail.com");
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

        // for(int i=0;i<24;i++)
        // {
        // courses.get(i).setImage(images.get(i));
        // }
        // courseRepo.saveAll(courses);
        // courseRepo.flush();

        // Seed categories
        List<Category> categories = CategoryGenerator.fromCourses(courses, List.of("AI", "Web", "Business", "Data"));
        categoryRepo.saveAll(categories);

        // Seed enrollments for first few courses
        List<Enrollment> enrollments = EnrollmentGenerator.forStudentsAndCourses(students, courses.subList(0, 5));
        enrollmentRepo.saveAll(enrollments);

        // Seed certificates (optional)
        List<Certificate> certificates = CertificateGenerator.fromCourses(courses);
        certificateRepo.saveAll(certificates);

        // Seed Modules
        List<Module> modules = ModuleGenerator.fromCourses(courses);
        moduleRepo.saveAll(modules);

        // Seed Sections
        List<Section> sections = SectionGenerator.fromModules(modules);
        sectionRepo.saveAll(sections);

        // Seed Materials
        List<Material> materials = MaterialGenerator.fromSections(sections);
        materialRepo.saveAll(materials);

        // Seed Material Completions
        List<MaterialCompletion> materialCompletions = MaterialCompletionGenerator
                .forEnrollmentsAndMaterials(enrollments, materials);
        materialCompletionRepo.saveAll(materialCompletions);

        // Seed Course Completions
        List<CourseCompletion> courseCompletions = CourseCompletionGenerator
                .forEnrollmentsWithMaterialProgress(enrollments);
        courseCompletionRepo.saveAll(courseCompletions);

        // Seed Quizzes
        List<Quiz> quizzes = QuizGenerator.fromMaterials(materials);
        quizRepo.saveAll(quizzes);

        // Seed questions
        List<Question> questions = QuestionGenerator.fromQuizzes(quizzes);
        questionRepo.saveAll(questions);

        // Seed Reviews
        List<Review> reviews = ReviewGenerator.generateReviews(courses, students);
        reviewRepo.saveAll(reviews);

        // Seed LearningPaths
        List<LearningPath> learningPaths = LearningPathGenerator.generateLearningPaths(orgs, courses);
        learningPathRepo.saveAll(learningPaths);

        // Seed Skills

        List<String> defaultSkillNames = List.of(
                "Java Programming",
                "Python Programming",
                "Web Development",
                "Data Analysis",
                "Machine Learning",
                "Database Design",
                "Software Architecture",
                "DevOps",
                "Cloud Computing",
                "Mobile Development");

        List<Skill> skills = SkillGenerator.generateSkills(defaultSkillNames);
        skillRepo.saveAll(skills);
    }

    @Transactional
    public void clean() {
        skillRepo.deleteAll();
        learningPathRepo.deleteAll();
        questionRepo.deleteAll();
        reviewRepo.deleteAll();
        quizRepo.deleteAll();
        courseCompletionRepo.deleteAll();
        materialCompletionRepo.deleteAll();
        materialRepo.deleteAll();
        sectionRepo.deleteAll();
        moduleRepo.deleteAll();
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
