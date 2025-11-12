package com.sistem.monitoring.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sistem.monitoring.models.CompanyModel;
import com.sistem.monitoring.models.CompanySupervisorModel;
import com.sistem.monitoring.models.UserModel;
import com.sistem.monitoring.services.CompanyService;
import com.sistem.monitoring.services.CompanySupervisorService;
import com.sistem.monitoring.services.UserService;
import org.springframework.web.bind.annotation.PutMapping;



@Controller
@RequestMapping("/comp-spv")
public class CompanySupervisorController {
    
    @Autowired
    private CompanySupervisorService companySupervisorService;

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService; 


    @GetMapping
    public String getAllDataFromCompanySupervisor(Model model){
       model.addAttribute("cspv", companySupervisorService.getCompanySupervisor());
        return "CompanySupervisorView/index";
    }

    @GetMapping("profile/{id}")
    public String getCompanySupervisorById(@PathVariable Long id){
        companySupervisorService.getCompanysupervisorById(id);
        return "CompanySupervisorView/profile";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model){
        CompanySupervisorModel spv = new CompanySupervisorModel();
        model.addAttribute("cspv",spv);
        model.addAttribute("companies", companyService.getAllCompanyData());
        return "CompanySupervisorView/create-form";

    }

    @PostMapping
    public String saveSupervisorData(@ModelAttribute CompanySupervisorModel spv, 
                                     @RequestParam("companyId") Long companyId ){
        UserModel user = spv.getUser();
        if (user == null) {
            user = new UserModel();
        }
        user.setRole(UserModel.Role.Company_Supervisor);
        UserModel savedUser = userService.createNewUser(user, null,null,"","",null,"");
        if (spv.getJobTitle() == null){
            spv.setJobTitle("");
        }
        if (spv.getPhoneNumber() == null ){
            spv.setPhoneNumber("");
        }

        spv.setUser(savedUser);
        CompanyModel company = companyService.getCompanyById(companyId)
                                .orElseThrow(()-> new RuntimeException("company "+ companyId + "not found"));
        spv.setCompany(company);
        companySupervisorService.createCompanySupervisor(spv,company);
        return "redirect:/comp-spv";
    }

    @GetMapping("edit/{id}")
    public String editDataSchoolsupervisor(@PathVariable Long id, Model model){
        CompanySupervisorModel spv = companySupervisorService.getCompanysupervisorById(id)
                                        .orElseThrow(()-> new RuntimeException("not found"));
            model.addAttribute("cspv", spv);
            model.addAttribute("companies", companyService.getAllCompanyData());
            return "CompanySupervisorView/edit-form";
    }

    @PutMapping("/{id}")
    public String updateSupervisor(
            @PathVariable Long id,
            @ModelAttribute("cspv") CompanySupervisorModel formSpv,
            @RequestParam("companyId") Long companyId) {

        System.out.println("DEBUG updateCompanySupervisor id=" + id + " companyId=" + companyId);
        if (formSpv.getUser() != null) {
            System.out.println("DEBUG user email=" + formSpv.getUser().getEmail());
        }

        companySupervisorService.updateCompanySupervisor(id, formSpv, companyId);
        return "redirect:/comp-spv";
    }


    @DeleteMapping("/{id}")
    public String deleteCompanySupervisor(@PathVariable Long id){
        companySupervisorService.deleteCompanySupervisor(id);
        return "redirect:/comp-spv";
    }



}
