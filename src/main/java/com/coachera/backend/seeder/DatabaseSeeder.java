package com.coachera.backend.seeder;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coachera.backend.entity.*;
import com.coachera.backend.generator.*;
import com.coachera.backend.repository.*;

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
        // Seed users and roles
        List<User> users = seedUsers();
        
        // Seed entities by role
        List<Student> students = seedStudents(users);
        List<Instructor> instructors = seedInstructors(users);
        List<Organization> orgs = seedOrganizations(users);
        
        // Seed course-related entities
        List<Course> courses = seedCourses(orgs);
        seedCategories(courses);
        List<Enrollment> enrollments = seedEnrollments(students, courses);
        seedCertificates(courses);
        
        // Seed course structure
        List<com.coachera.backend.entity.Module> modules = seedModules(courses);
        List<Section> sections = seedSections(modules);
        List<Material> materials = seedMaterials(sections);
        
        // Seed completions and assessments
        seedCompletions(enrollments, materials);
        seedQuizzesAndQuestions(materials);
        seedReviews(courses, students);
        
        // Seed learning paths and skills
        seedLearningPaths(orgs, courses);
        seedSkills();
    }

    private List<User> seedUsers() {
        List<User> users = userGenerator.generate(15);
        
        // Set specific users
        users.get(0).setRole("STUDENT");
        users.get(0).setEmail("student@gmail.com");
        users.get(1).setRole("INSTRUCTOR");
        users.get(1).setEmail("instructer@gmail.com");
        users.get(2).setRole("ORGANIZATION");
        users.get(2).setEmail("organization@gmail.com");
        
        // Set remaining users
        for (int i = 3; i < users.size(); i++) {
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
        
        return userRepo.saveAll(users);
    }

    private List<Student> seedStudents(List<User> users) {
        List<User> studentUsers = filterUsersByRole(users, "STUDENT");
        List<Student> students = StudentGenerator.fromUsers(studentUsers);
        return studentRepo.saveAll(students);
    }

    private List<Instructor> seedInstructors(List<User> users) {
        List<User> instructorUsers = filterUsersByRole(users, "INSTRUCTOR");
        List<Instructor> instructors = InstructorGenerator.fromUsers(instructorUsers);
        return instructorRepo.saveAll(instructors);
    }

    private List<Organization> seedOrganizations(List<User> users) {
        List<User> orgUsers = filterUsersByRole(users, "ORGANIZATION");
        List<Organization> orgs = OrganizationGenerator.fromUsers(orgUsers);
        return orgRepo.saveAll(orgs);
    }

    private List<Course> seedCourses(List<Organization> orgs) {
        List<Course> courses = CourseGenerator.fromOrg(orgs);
        return courseRepo.saveAll(courses);
    }

    private void seedCategories(List<Course> courses) {
        List<Category> categories = CategoryGenerator.fromCourses(courses, 
            List.of("AI", "Web", "Business", "Data"));
        categoryRepo.saveAll(categories);
    }

    private List<Enrollment> seedEnrollments(List<Student> students, List<Course> courses) {
        List<Enrollment> enrollments = EnrollmentGenerator.forStudentsAndCourses(students, courses.subList(0, 5));
        return enrollmentRepo.saveAll(enrollments);
    }

    private void seedCertificates(List<Course> courses) {
        List<Certificate> certificates = CertificateGenerator.fromCourses(courses);
        certificateRepo.saveAll(certificates);
    }

    private List<com.coachera.backend.entity.Module> seedModules(List<Course> courses) {
        List<com.coachera.backend.entity.Module> modules = ModuleGenerator.fromCourses(courses);
        return moduleRepo.saveAll(modules);
    }

    private List<Section> seedSections(List<com.coachera.backend.entity.Module> modules) {
        List<Section> sections = SectionGenerator.fromModules(modules);
        return sectionRepo.saveAll(sections);
    }

    private List<Material> seedMaterials(List<Section> sections) {
        List<Material> materials = MaterialGenerator.fromSections(sections);
        return materialRepo.saveAll(materials);
    }

    private void seedCompletions(List<Enrollment> enrollments, List<Material> materials) {
        List<MaterialCompletion> materialCompletions = MaterialCompletionGenerator
                .forEnrollmentsAndMaterials(enrollments, materials);
        materialCompletionRepo.saveAll(materialCompletions);

        List<CourseCompletion> courseCompletions = CourseCompletionGenerator
                .forEnrollmentsWithMaterialProgress(enrollments);
        courseCompletionRepo.saveAll(courseCompletions);
    }

    private void seedQuizzesAndQuestions(List<Material> materials) {
        List<Quiz> quizzes = QuizGenerator.fromMaterials(materials);
        quizRepo.saveAll(quizzes);

        List<Question> questions = QuestionGenerator.fromQuizzes(quizzes);
        questionRepo.saveAll(questions);
    }

    private void seedReviews(List<Course> courses, List<Student> students) {
        List<Review> reviews = ReviewGenerator.generateReviews(courses, students);
        reviewRepo.saveAll(reviews);
    }

    private void seedLearningPaths(List<Organization> orgs, List<Course> courses) {
        List<LearningPath> learningPaths = LearningPathGenerator.generateLearningPaths(orgs, courses);
        learningPathRepo.saveAll(learningPaths);
    }

    private void seedSkills() {
        List<String> skillNames = List.of(
                "Java Programming", "Python Programming", "Web Development", "Data Analysis",
                "Machine Learning", "Database Design", "Software Architecture", "DevOps",
                "Cloud Computing", "Mobile Development");
        List<Skill> skills = SkillGenerator.generateSkills(skillNames);
        skillRepo.saveAll(skills);
    }

    private List<User> filterUsersByRole(List<User> users, String role) {
        return users.stream()
                .filter(user -> role.equalsIgnoreCase(user.getRole()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void clean() {
        List.of(skillRepo, learningPathRepo, questionRepo, reviewRepo, quizRepo,
                courseCompletionRepo, materialCompletionRepo, materialRepo, sectionRepo,
                moduleRepo, certificateRepo, enrollmentRepo, courseRepo, categoryRepo,
                orgRepo, instructorRepo, studentRepo, userRepo)
                .forEach(repo -> repo.deleteAll());
    }
}
