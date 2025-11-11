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
import org.springframework.web.bind.annotation.RequestParam;

import com.sistem.monitoring.models.UserModel;
import com.sistem.monitoring.services.UserService;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String listAllUser(Model model){
        model.addAttribute("users",userService.getAllUser());
        return "UserView/index";
    }
    @GetMapping("/{id}")
    public String getUserDetail(@PathVariable Long id, Model model) {
            UserModel user = userService.getUserById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
            model.addAttribute("user", user);
            return "UserView/profile";
        }

    @GetMapping("/create")
    public String showCreateForm(Model model){
        model.addAttribute("user",new UserModel());
        return "UserView/create-form";
    }
    
    @PostMapping
    public String saveUser(@ModelAttribute UserModel user,
                       @RequestParam(required = false) String studentNumber,
                       @RequestParam(required = false) String studentPhone,
                       @RequestParam(required = false) String companyName,
                       @RequestParam(required = false) String jobTitle,
                       @RequestParam(required = false) String employeeIdNumber,
                       @RequestParam(required = false) String schoolPhone) {

    userService.createNewUser(
        user, studentNumber, studentPhone,
        companyName, jobTitle,
        employeeIdNumber, schoolPhone
    );

    return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable Long id, Model model){
        UserModel user =userService.getUserById(id).orElseThrow(()->new RuntimeException("no user found"));
        model.addAttribute("user", user);
        model.addAttribute("role", UserModel.Role.values());
        return "UserView/edit-form";
    }
    @PutMapping("/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute UserModel user){
            user.setUserId(id);
            userService.updateUser(id, user);
            return "redirect:/users";
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return "redirect:/users";
    }
    
}
