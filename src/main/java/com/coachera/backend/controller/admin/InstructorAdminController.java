package com.coachera.backend.controller.admin;

import com.coachera.backend.dto.InstructorDTO; // 1. استيراد InstructorDTO
import com.coachera.backend.service.InstructorService; // 2. استيراد InstructorService
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin")
public class InstructorAdminController {

    private final InstructorService instructorService;

    public InstructorAdminController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping("/instructors")
    public String listInstructors(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        model.addAttribute("pageTitle", "Instructors List");
        model.addAttribute("activePage", "instructors"); 
        Pageable pageable = PageRequest.of(page, size);
        Page<InstructorDTO> instructorPage = instructorService.getInstructors(pageable); 

        model.addAttribute("instructorPage", instructorPage);

        int totalPages = instructorPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "admin/instructors"; 
    }

    @PostMapping("/instructors/delete/{id}")
    public String deleteInstructor(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            instructorService.deleteInstructor(id);
            redirectAttributes.addFlashAttribute("successMessage", "Instructor deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting instructor: " + e.getMessage());
        }
        return "redirect:/admin/instructors";
    }
}
