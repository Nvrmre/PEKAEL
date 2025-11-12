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

import com.sistem.monitoring.models.AttendanceModel;
import com.sistem.monitoring.services.AttendanceService;

@Controller
@RequestMapping("/attendances")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;


    @GetMapping
    public String showAllAttendance(Model model){
        model.addAttribute("attend",attendanceService.getAllAttendances());
        return "AttendanceView/index";
    }

    @GetMapping("/create")
    public String showFormCreate(Model model){
        AttendanceModel attend = new AttendanceModel();
        model.addAttribute("attend", attend);
        return "AttendanceView/create-form";
    }

    @PostMapping
    public String saveData(@ModelAttribute AttendanceModel attend){
        attendanceService.createAttendance(attend);
        return "redirect:/attendances";
    }
    
    @GetMapping("edit/{id}")
    public String editData(@PathVariable Long id, Model model){
        AttendanceModel attend = attendanceService.getAttendanceById(id).orElseThrow(()-> new RuntimeException("Attendance Not found"));
        model.addAttribute("attend",attend);
        return "AttendanceView/edit-form";
    }

    @PutMapping("/{id}")
    public String updateData(@PathVariable Long id, @ModelAttribute AttendanceModel attend){
        attendanceService.updateAttendance(id, attend);
        return "redirect:/attendances";
    }

    @DeleteMapping("/{id}")
    public String deleteData(@PathVariable Long id){
        attendanceService.deleteAttendance(id);
        return "redirect:/attendances";
    }
}
