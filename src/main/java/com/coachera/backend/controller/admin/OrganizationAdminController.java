package com.coachera.backend.controller.admin;

import com.coachera.backend.dto.OrganizationDTO; // 1. استيراد OrganizationDTO
import com.coachera.backend.service.OrganizationService; // 2. استيراد OrganizationService
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
public class OrganizationAdminController {

    private final OrganizationService organizationService;

    public OrganizationAdminController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping("/organizations")
    public String listOrganizations(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        model.addAttribute("pageTitle", "Organizations List");
        model.addAttribute("activePage", "organizations"); 

        Pageable pageable = PageRequest.of(page, size);
        Page<OrganizationDTO> organizationPage = organizationService.getOrganizations(pageable); 

        model.addAttribute("organizationPage", organizationPage);

        int totalPages = organizationPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "admin/organizations"; 
    }

    @PostMapping("/organizations/delete/{id}")
    public String deleteOrganization(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            organizationService.deleteOrganization(id);
            redirectAttributes.addFlashAttribute("successMessage", "Organization deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting organization: " + e.getMessage());
        }
        return "redirect:/admin/organizations";
    }
}
