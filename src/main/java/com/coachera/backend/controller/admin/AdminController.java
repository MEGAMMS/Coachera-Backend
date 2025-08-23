package com.coachera.backend.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/home")
    public String adminHomePage(Model model) {
        // Set the page title
        model.addAttribute("pageTitle", "Dashboard"); 
        
        
        Map<String, Object> statistics = Map.of(
            "totalStudents", "1,250",
            "totalInstructors", 150,
            "activeSubscriptions", 840,
            "monthlySales", "$15,750"
        );
        
        // Add the statistics map to the model
        model.addAttribute("stats", statistics);
        
        return "admin/home"; 
    }
    
    // Add a new method for the course requests page
    @GetMapping("/course-requests")
    public String courseRequestsPage(Model model) {
        model.addAttribute("pageTitle", "Course Requests");
        
        // Add logic to fetch course requests from your service
        // model.addAttribute("requests", courseRequestService.getPending());
        
        // You need to create a 'course-requests.html' file for this
        return "admin/course-requests"; 
    }
}
