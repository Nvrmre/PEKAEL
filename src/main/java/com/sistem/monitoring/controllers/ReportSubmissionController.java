package com.sistem.monitoring.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.sistem.monitoring.models.ReportSubmissionModel;
import com.sistem.monitoring.repositories.ReportSubmissionRepository;
import com.sistem.monitoring.services.PlacementService;
import com.sistem.monitoring.services.ReportSubmissionService;

@Controller
@RequestMapping("/report-submissions")
public class ReportSubmissionController {


    private final ReportSubmissionService reportSubmissionService;
    private final PlacementService placementService;
    private final Path uploadDir = Paths.get("src/main/resources/static/uploads/report-submission/");

    public ReportSubmissionController(ReportSubmissionService reportService,
                                      PlacementService placementService) {
        this.reportSubmissionService = reportService;
        this.placementService = placementService;
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            // log error
        }
    }
    @GetMapping
    public String showAllSubmission(Model model){
        model.addAttribute("submission", reportSubmissionService.getAllSubmission());
        return "ReportSubmissionView/index";
    }
    
    @GetMapping("/create")
    public String showCreateForm(Model model){
        ReportSubmissionModel report = new ReportSubmissionModel();
        model.addAttribute("submission", report);
        model.addAttribute("placement", placementService.getAllPlacement());
        return "ReportSubmissionView/create-form";
    }

    @PostMapping
    public String saveReport(@ModelAttribute("submission") ReportSubmissionModel submission,
                            @RequestParam("fileUpload") MultipartFile fileUpload) {

        if (submission.getStatus() == null) {
            submission.setStatus(ReportSubmissionModel.Status.PENDING);
        }

        if (fileUpload != null && !fileUpload.isEmpty()) {
            String filename = System.currentTimeMillis() + "-" + fileUpload.getOriginalFilename();
            try {
                Path dest = uploadDir.resolve(filename).normalize();
                Files.copy(fileUpload.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

                submission.setFilePath("/uploads/report-submission/" + filename);
            } catch (IOException e) {
                throw new RuntimeException("Gagal upload file", e);
            }
        }

        reportSubmissionService.createSubmission(submission);
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
