package com.coachera.backend.controller.admin;

import com.coachera.backend.dto.StudentDTO;
import com.coachera.backend.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller; // Error 1: Missing @Controller
import org.springframework.ui.Model; // Error 2: Wrong Model import
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping; // Error 3: Missing @RequestMapping
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller // This annotation is required for Spring to recognize this as a controller
@RequestMapping("/admin") // This defines the base URL for all methods in this class
public class StudentAdminController {

    private final StudentService studentService;

    public StudentAdminController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/students")
    public String listStudents(
            Model model, // Corrected the import for Model
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        model.addAttribute("pageTitle", "Students List");

        model.addAttribute("activePage", "students"); // Add active page identifier

        // Error 4: The type must be Pageable, not PaginationRequest
        Pageable pageable = PageRequest.of(page, size);

        // Call the service to get the real, paginated data
        Page<StudentDTO> studentPage = studentService.getStudents(pageable);

        model.addAttribute("studentPage", studentPage);

        // This part remains the same as it works with the Page object
        int totalPages = studentPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "admin/students";
    }

    @PostMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("successMessage", "Student deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting student.");
        }
        return "redirect:/admin/students";
    }
}
