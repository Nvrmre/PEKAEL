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

import com.sistem.monitoring.models.ReportSubmissionModel;
import com.sistem.monitoring.services.ReportSubmissionService;

@Controller
@RequestMapping("/report-submissions")
public class ReportSubmissionController {

    @Autowired
    private ReportSubmissionService reportSubmissionService;

    @GetMapping
    public String showAllSubmission(Model model){
        model.addAttribute("submission", reportSubmissionService.getAllSubmission());
        return "ReportSubmissionView/index";
    }
    
    @GetMapping("/create")
    public String showCreateForm(Model model){
        ReportSubmissionModel report = new ReportSubmissionModel();
        model.addAttribute("submission", report);
        return "ReportSubmissionView/create-form";
    }

    @PostMapping
    public String saveData(@ModelAttribute ReportSubmissionModel report){
        reportSubmissionService.createSubmission(report);
        return "redirect:/report-submissions";
    }

    @GetMapping("/edit/{id}")
    public String editData(@PathVariable Long id,Model model){
        ReportSubmissionModel report = reportSubmissionService.getSubmissionById(id).orElseThrow(()-> new RuntimeException("Not found"));
        model.addAttribute("submission",report);
        return "ReportSubmissionView/edit-form";
    }

    @PutMapping
    public String updateData(@PathVariable Long id, @ModelAttribute ReportSubmissionModel report){
        reportSubmissionService.updateReportSubmission(id, report);
        return "redirect:/report-submissions";  
    }

    @DeleteMapping("/{id}")
    public String deleteData(@PathVariable Long id){
        reportSubmissionService.deleteReport(id);
        return "redirect:/report-submissions";
    }
}
