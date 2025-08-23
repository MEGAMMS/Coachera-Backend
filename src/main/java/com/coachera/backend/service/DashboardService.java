package com.coachera.backend.service;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final StudentService studentService;
    private final InstructorService instructorService;
    private final OrganizationService organizationService;
    private final EnrollmentService enrollmentService;

    // Constructor-based dependency injection is the recommended approach
    public DashboardService(StudentService studentService, InstructorService instructorService,
                            OrganizationService organizationService, EnrollmentService enrollmentService) {
        this.studentService = studentService;
        this.instructorService = instructorService;
        this.organizationService = organizationService;
        this.enrollmentService = enrollmentService;
    }

    public Map<String, Object> getDashboardStatistics() {
        long totalStudents = studentService.countStudents();
        long totalInstructors = instructorService.countInstructors();
        long totalorganizations = organizationService.countOrganizations();
        long activeSubscriptions = enrollmentService.countenrollments();
        

        return Map.of(
            "totalStudents", totalStudents,
            "totalInstructors", totalInstructors,
            "activeSubscriptions", activeSubscriptions,
            "totalorganizations" , totalorganizations,
            "monthlySales", "$15,750"

        );
    }
}