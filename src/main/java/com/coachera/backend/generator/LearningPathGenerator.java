package com.coachera.backend.generator;

import com.coachera.backend.entity.*;
import org.instancio.Instancio;
import org.instancio.Select;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class LearningPathGenerator {

    private static final Random random = new Random();
    private static final String[] PATH_TYPES = {
        "Beginner", "Intermediate", "Advanced", "Professional", 
        "Specialized", "Comprehensive", "Accelerated"
    };
    
    private static final String[] DOMAINS = {
        "Web Development", "Data Science", "Mobile Development", 
        "Cloud Computing", "Cybersecurity", "AI/ML", "DevOps"
    };

    public static List<LearningPath> generateLearningPaths(List<Organization> organizations, List<Course> courses) {
        if (organizations == null || organizations.isEmpty() || courses == null) {
            throw new IllegalArgumentException("Organizations and courses lists cannot be null or empty");
        }

        return organizations.stream()
            .map(organization -> {
                // Ensure organization is persisted
                if (organization.getId() == null) {
                    throw new IllegalStateException("Organization must be persisted first");
                }

                LearningPath learningPath = Instancio.of(LearningPath.class)
                    .ignore(Select.field(LearningPath::getId))
                    .supply(Select.field(LearningPath::getOrganization), () -> organization)
                    .supply(Select.field(LearningPath::getTitle), () -> generatePathTitle())
                    .supply(Select.field(LearningPath::getDescription), () -> generatePathDescription())
                    .ignore(Select.field(LearningPath::getImage))
                    .ignore(Select.field(LearningPath::getCourses))
                    .ignore(Select.field(LearningPath::getImage))
                    .create();

                // Add 3-8 courses to the learning path
                int courseCount = random.nextInt(6) + 3;
                AtomicInteger orderIndex = new AtomicInteger(0);
                
                Set<Course> selectedCourses = courses.stream()
                    .filter(c -> c.getId() != null) // Ensure courses are persisted
                    .collect(Collectors.toSet())
                    .stream()
                    .limit(courseCount)
                    .collect(Collectors.toSet());

                selectedCourses.forEach(course -> 
                    learningPath.addCourse(course, orderIndex.getAndIncrement()));

                return learningPath;
            })
            .collect(Collectors.toList());
    }

    private static String generatePathTitle() {
        return PATH_TYPES[random.nextInt(PATH_TYPES.length)] + " " + 
               DOMAINS[random.nextInt(DOMAINS.length)] + " Learning Path";
    }

    private static String generatePathDescription() {
        return "This comprehensive learning path covers all essential aspects of " +
               DOMAINS[random.nextInt(DOMAINS.length)] + " from basic to advanced concepts, " +
               "including hands-on projects and real-world applications.";
    }

    // Alternative version with fixed course count
    public static List<LearningPath> generateLearningPaths(
            List<Organization> organizations, 
            List<Course> courses,
            int minCourses,
            int maxCourses) {
        
        return organizations.stream()
            .map(organization -> {
                LearningPath lp = Instancio.of(LearningPath.class)
                    .ignore(Select.field(LearningPath::getId))
                    .supply(Select.field(LearningPath::getOrganization), () -> organization)
                    .create();

                int courseCount = random.nextInt(maxCourses - minCourses + 1) + minCourses;
                AtomicInteger orderIndex = new AtomicInteger(0);
                
                courses.stream()
                    .filter(c -> c.getId() != null)
                    .limit(courseCount)
                    .forEach(course -> lp.addCourse(course, orderIndex.getAndIncrement()));

                return lp;
            })
            .collect(Collectors.toList());
    }

    // Version for creating learning paths with specific courses
    public static LearningPath createWithCourses(Organization org, List<Course> coursesToInclude) {
        LearningPath lp = Instancio.of(LearningPath.class)
            .ignore(Select.field(LearningPath::getId))
            .supply(Select.field(LearningPath::getOrganization), () -> org)
            .create();

        AtomicInteger orderIndex = new AtomicInteger(0);
        coursesToInclude.forEach(course -> 
            lp.addCourse(course, orderIndex.getAndIncrement()));

        return lp;
    }
}