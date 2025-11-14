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

import com.sistem.monitoring.models.SchoolSupervisorModel;
import com.sistem.monitoring.models.UserModel;
import com.sistem.monitoring.services.SchoolSupervisorService;
import com.sistem.monitoring.services.UserService;

@Controller
@RequestMapping("/sch-spv")
public class SchoolSupervisorController {
    
    @Autowired
    private SchoolSupervisorService schoolSupervisorService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String getAllDataFromSchoolSupervisor(Model model){
        model.addAttribute("sspv", schoolSupervisorService.getSchoolSupervisor()) ;
        return "SchoolSupervisorView/index";
    }

    @GetMapping("/{id}")
    public String getDataSupervisorById(@PathVariable Long id){
        schoolSupervisorService.getSchoolsupervisorById(id);
        return "SchoolSupervisorView/profile";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model){
        SchoolSupervisorModel spv = new SchoolSupervisorModel();
        spv.setUser(new UserModel());
        model.addAttribute("sspv",spv);
        return "SchoolSupervisorView/create-form";
    }

    @PostMapping
    public String saveSupervisorData(@ModelAttribute SchoolSupervisorModel spv){
        UserModel user = spv.getUser();
        if (user == null) {
            user = new UserModel(); 
        }

        user.setRole(UserModel.Role.School_Supervisor);
        UserModel savedUser = userService.createNewUser(user,null,null,null,null,"","");
        if (spv.getEmployeeIdNumber() == null) {
            spv.setEmployeeIdNumber("");
        }
        if (spv.getSchoolSupervisorPhone() == null) {
            spv.setSchoolSupervisorPhone("");
        }
        spv.setUser(savedUser);
        schoolSupervisorService.createSchoolSupervisor(spv);
        return "redirect:/sch-spv";
    }

    @GetMapping("edit/{id}")
    public String editDataSchoolSupervisor(@PathVariable Long id, Model model){
        SchoolSupervisorModel spv = schoolSupervisorService.getSchoolsupervisorById(id)
                                    .orElseThrow(()-> new RuntimeException("User Not found to edit"));
        model.addAttribute("sspv",spv);
        return "SchoolSupervisorView/edit-form";
    }

    @PutMapping("/{id}")
    public String updateSupervisor(@PathVariable Long id, @ModelAttribute SchoolSupervisorModel spv){
        schoolSupervisorService.updateSchoolSupervisor(id, spv);
        return "redirect:/sch-spv";
    }

    @DeleteMapping("/{id}")
    public String deleteSchoolSupervisor(@PathVariable Long id){
        schoolSupervisorService.deleteSchoolSupervisor(id);
        return "redirect:/sch-spv";
    }
}
