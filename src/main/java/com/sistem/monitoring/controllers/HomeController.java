package com.sistem.monitoring.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.sistem.monitoring.services.UserService;
import com.sistem.monitoring.services.SchoolSupervisorService;
import com.sistem.monitoring.services.CompanySupervisorService;
import com.sistem.monitoring.services.StudentServices;
import com.sistem.monitoring.services.PlacementService;

@Controller
public class HomeController {

    // contoh: inject beberapa service kalau mau tampilkan ringkasan statistik
    private final UserService userService;
    private final StudentServices studentServices;
    private final CompanySupervisorService companySupervisorService;
    private final SchoolSupervisorService schoolSupervisorService;
    private final PlacementService placementService;

    @Autowired
    public HomeController(
            UserService userService,
            StudentServices studentServices,
            CompanySupervisorService companySupervisorService,
            SchoolSupervisorService schoolSupervisorService,
            PlacementService placementService) {
        this.userService = userService;
        this.studentServices = studentServices;
        this.companySupervisorService = companySupervisorService;
        this.schoolSupervisorService = schoolSupervisorService;
        this.placementService = placementService;
    }

    // redirect root ke /home (opsional)
    @GetMapping("/")
    public String root() {
        return "redirect:/index";
    }

    // Home page
    @GetMapping("/index")
    public String home(Model model, Principal principal) {
        // contoh ringkasan statistik sederhana
        model.addAttribute("totalUsers", userService.getAllUser().size());
        model.addAttribute("totalStudents", studentServices.getAllUserStudent().size());
        model.addAttribute("totalCompanySpv", companySupervisorService.getCompanySupervisor().size());
        model.addAttribute("totalSchoolSpv", schoolSupervisorService.getSchoolSupervisor().size());
        model.addAttribute("totalPlacements", placementService.getAllPlacement().size());

        // jika ingin tampilkan username yang sedang login (jika pakai Spring Security)
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }

        return "index"; // akan merender templates/home.html
    }
}
