package com.coachera.backend.controller.admin;

import com.coachera.backend.dto.CourseDTO;
import com.coachera.backend.service.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class CourseAdminController {

    private final CourseService courseService;

    public CourseAdminController(CourseService courseService) {
        this.courseService = courseService;
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

    // We will add the course details endpoint here later
    // @GetMapping("/courses/{id}")
    // public String viewCourseDetails(...) { ... }
}
