package com.coachera.backend.controller.admin;

import com.coachera.backend.dto.CourseDTO;
import com.coachera.backend.service.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
            @RequestParam(defaultValue = "10") int size) {

        model.addAttribute("pageTitle", "Course Management");
        model.addAttribute("activePage", "course-requests"); // To highlight the link in sidebar

        Pageable pageable = PageRequest.of(page, size);
        Page<CourseDTO> coursePage = courseService.getCourses(pageable); // Using your existing service method

        model.addAttribute("coursePage", coursePage);

        return "admin/course-requests"; // The name of our new HTML file
    }

    // We will add the course details endpoint here later
    // @GetMapping("/courses/{id}")
    // public String viewCourseDetails(...) { ... }
}
