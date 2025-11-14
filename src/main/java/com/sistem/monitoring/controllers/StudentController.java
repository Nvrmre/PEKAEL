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


import com.sistem.monitoring.models.StudentModel;
import com.sistem.monitoring.models.UserModel;
import com.sistem.monitoring.services.StudentServices;
import com.sistem.monitoring.services.UserService;

@Controller
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentServices studentServices;

    @Autowired
    private UserService userService; // pastikan ada


    @GetMapping
    public String listOfStudent(Model model){
        model.addAttribute("students",studentServices.getAllUserStudent());
        return "StudentView/index";
    }

    @GetMapping("/create")
    public String createStudentData(Model model){
        StudentModel student = new StudentModel();
        student.setUser(new UserModel());
        model.addAttribute("student", student);
        return "StudentView/create-form";
    }

    @PostMapping
    public String saveStudentData(@ModelAttribute StudentModel student){
        UserModel user = student.getUser();
        if (user == null) {
            user = new UserModel();
        }
        
        user.setRole(UserModel.Role.Student);
        UserModel savedUser = userService.createNewUser(user,"","",null,null,null,null);
        if (student.getStudentNumber() == null) {
            student.setStudentNumber("");
        }
        if (student.getPhoneNumber() == null) {
            student.setPhoneNumber("");
        }
        if (user.getPassword() == null){
            user.setPassword("");
        }
        student.setUser(savedUser);
        studentServices.createNewUserStudent(student);

        return "redirect:/students";
    }

    @GetMapping("edit/{id}")
    public String editStudentData(@PathVariable Long id, Model model){
        StudentModel student = studentServices.getUserStudentById(id).orElseThrow(()-> new RuntimeException("Not found the user with id " + id));
        model.addAttribute("student", student);
        return "StudentView/edit-form";

    }

    @PutMapping("/{id}")
    public String updateStudent(@PathVariable Long id, @ModelAttribute StudentModel student) {
        studentServices.updateUserStudent(id, student);
        return "redirect:/students";
    }
    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Long id){
        studentServices.deleteStudent(id);
        return "redirect:/students";
    }
    
}
