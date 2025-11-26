package com.sistem.monitoring.controllers;
import com.sistem.monitoring.services.PlacementService;
import com.sistem.monitoring.services.StudentServices;
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

import com.sistem.monitoring.models.GradeModel;
import com.sistem.monitoring.services.GradeService;

@Controller
@RequestMapping("/grades")
public class GradeController {

    @Autowired
    private final StudentServices studentServices;

    @Autowired
    private final PlacementService placementService;
    

    @Autowired
    private GradeService gradeService;

    GradeController(PlacementService placementService, StudentServices studentServices) {
        this.placementService = placementService;
        this.studentServices = studentServices;
    }

    @GetMapping
    public String showAllGrade(Model model){
        model.addAttribute("grades", gradeService.getAllGrade());
        return "GradeView/index";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model){
        GradeModel grade = new GradeModel();
        model.addAttribute("grades", grade);
        model.addAttribute("placements", placementService.getAllPlacement());
        model.addAttribute("students", studentServices.getAllUserStudent());
        return "GradeView/create-form";
    }

    @PostMapping
    public String saveData(@ModelAttribute GradeModel grades){
        gradeService.createGrade(grades);
        return "redirect:/grades";
    }

    @GetMapping("/edit/{id}")
    public String showCreateForm(@PathVariable Long id, Model model){
        GradeModel grade = gradeService.getGradeById(id).orElseThrow(()-> new RuntimeException("Not Found"));
        model.addAttribute("grades", grade);
        return "GradeView/edit-form";
    }

    @PutMapping("/{id}")
    public String updateData(@PathVariable Long id, @ModelAttribute GradeModel grade){
        gradeService.updateGrade(id, grade);
        return "redirect:/grades";
    }

    @DeleteMapping("/{id}")
    public String deleteData(@PathVariable Long id){
        gradeService.deleteGrade(id);
        return "redirect:/grades";
    }
}
