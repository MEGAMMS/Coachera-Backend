package com.coachera.backend.controller.admin;

import com.coachera.backend.dto.CourseDTO;
import com.coachera.backend.dto.InstructorDTO;
import com.coachera.backend.dto.ModuleDTO;
import com.coachera.backend.service.CourseService;
import com.coachera.backend.service.InstructorService;
import com.coachera.backend.service.ModuleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class CourseAdminController {

    private final CourseService courseService;
    private final InstructorService instructorService;
    private final ModuleService moduleService;

    public CourseAdminController(CourseService courseService, InstructorService instructorService, ModuleService moduleService) {
        this.courseService = courseService;
        this.instructorService = instructorService;
        this.moduleService = moduleService;
    }

    @GetMapping("/courses")
    public String listCourses(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        model.addAttribute("pageTitle", "Course Management");
        model.addAttribute("activePage", "course-requests");

        // --- THIS IS THE FIX ---
        // We check the requested sort property. If it's "published" from the frontend,
        // we map it to the actual property name in the Course entity, which is likely "isPublished".
        String sortProperty = sortBy;
        if (sortBy.equals("published")) {
            sortProperty = "isPublished"; // **IMPORTANT: Change this if your entity field has a different name**
        }
        // --- END OF FIX ---

        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortProperty).ascending() : Sort.by(sortProperty).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CourseDTO> coursePage = courseService.getCourses(pageable);

        model.addAttribute("coursePage", coursePage);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDirection", sortDirection);

        return "admin/course-requests";
    }

    // --- NEW METHOD FOR COURSE DETAILS ---
    @GetMapping("/courses/{id}")
    public String viewCourseDetails(@PathVariable("id") Integer id, Model model) {
        // 1. Fetch the main course details
        CourseDTO course = courseService.getCourseById(id); // Assumes you have this service method
        model.addAttribute("course", course);

        // 2. Fetch the instructors for this course
        List<InstructorDTO> instructors;
        if (course.getInstructors() != null && !course.getInstructors().isEmpty()) {
            instructors = instructorService.getInstructorsByIds(new ArrayList<>(course.getInstructors())); // Assumes this service method
        } else {
            instructors = Collections.emptyList();
        }
        model.addAttribute("instructors", instructors);

        // 3. Fetch the course modules and their content
        List<ModuleDTO> modules = moduleService.getAllModulesByCourseId(id);
        model.addAttribute("modules", modules);

        model.addAttribute("pageTitle", course.getTitle());
        model.addAttribute("activePage", "course-requests");

        return "admin/course-details"; // The name of our new HTML file
    }

    // --- NEW METHODS FOR PUBLISHING ---
    @PostMapping("/courses/{id}/publish")
    public String publishCourse(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            courseService.publishCourse(id); // Assumes you have this service method
            redirectAttributes.addFlashAttribute("successMessage", "Course has been successfully published!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error publishing course: " + e.getMessage());
        }
        return "redirect:/admin/courses/" + id;
    }

    @PostMapping("/courses/{id}/unpublish")
    public String unpublishCourse(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            courseService.unpublishCourse(id); // Assumes you have this service method
            redirectAttributes.addFlashAttribute("successMessage", "Course has been successfully unpublished!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error unpublishing course: " + e.getMessage());
        }
        return "redirect:/admin/courses/" + id;
    }
}



