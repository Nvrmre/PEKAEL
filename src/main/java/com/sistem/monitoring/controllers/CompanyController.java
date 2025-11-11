package com.sistem.monitoring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sistem.monitoring.models.CompanyModel;
import com.sistem.monitoring.services.CompanyService;

@Controller
@RequestMapping("/companies")
public class CompanyController {
    
    @Autowired
    private CompanyService companyService;

    @GetMapping
    public String getAllCompanyData(Model model){
        model.addAttribute("companies",companyService.getAllCompanyData());
        return "CompanyView/index";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model){
        CompanyModel comp = new CompanyModel();
        model.addAttribute("comp",comp);
        return "CompanyView/create-form";
    }

    @PostMapping
    public String saveData(@ModelAttribute CompanyModel company){
        companyService.createCompanyData(company);
        return "redirect:/companies";
    }

    @GetMapping("edit/{id}")
    public String showFormEdit(@PathVariable Long id, Model model){
        CompanyModel comp = companyService.getCompanyById(id)
                            .orElseThrow(()-> new RuntimeException("company with id " + id + "not found"));
                model.addAttribute("comp",comp);
                return "CompanyView/edit-form";
    }

    @PutMapping
    public String updateDataCompany (@PathVariable Long id, @ModelAttribute CompanyModel comp){
        companyService.updateCompanyData(id, comp);
        return "redirect:/companies";
    }

    @DeleteMapping("/{id}")
    public String deleteDataCompany(@PathVariable Long id){
        companyService.deleteCompanyData(id);
        return "redirect:/companies";
    }

}
