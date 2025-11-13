package com.sistem.monitoring.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.sistem.monitoring.services.UserService;
import com.sistem.monitoring.services.SchoolSupervisorService;
import com.sistem.monitoring.services.CompanyService;
import com.sistem.monitoring.services.CompanySupervisorService;
import com.sistem.monitoring.services.DailyJournalService;
import com.sistem.monitoring.services.StudentServices;
import com.sistem.monitoring.services.PlacementService;

@Controller
public class HomeController {

   
    private final UserService userService;
    private final StudentServices studentServices;
    private final CompanySupervisorService companySupervisorService;
    private final SchoolSupervisorService schoolSupervisorService;
    private final PlacementService placementService;
    private final DailyJournalService dailyJournalService;
    private final CompanyService companyService;

    public HomeController(
            UserService userService,
            StudentServices studentServices,
            CompanySupervisorService companySupervisorService,
            SchoolSupervisorService schoolSupervisorService,
            PlacementService placementService,
            DailyJournalService dailyJournalService,
            CompanyService companyService ) {
        this.userService = userService;
        this.studentServices = studentServices;
        this.companySupervisorService = companySupervisorService;
        this.schoolSupervisorService = schoolSupervisorService;
        this.placementService = placementService;
        this.dailyJournalService = dailyJournalService;
        this.companyService = companyService;
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
        model.addAttribute("totalJournal", dailyJournalService.getAllJournal().size());
        model.addAttribute("totalCompany",companyService.getAllCompanyData().size());
       
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }

        return "index"; 
    }
}
