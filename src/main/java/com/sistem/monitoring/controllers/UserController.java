package com.sistem.monitoring.controllers;

import com.sistem.monitoring.models.CompanyModel;
import com.sistem.monitoring.models.UserModel;
import com.sistem.monitoring.services.CompanyService;
import com.sistem.monitoring.services.CompanySupervisorService;
import com.sistem.monitoring.services.UserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CompanySupervisorService companySupervisorService;

    @Autowired
    private CompanyService companyService;

    // ==========================================
    // 1. LIST USER (Hanya Administrator)
    // ==========================================
    @PreAuthorize("hasAuthority('Administrator','ADMINISTRATOR')")
    @GetMapping
    public String listAllUser(Model model){
        model.addAttribute("users", userService.getAllUser());
        return "UserView/index";
    }

    // ==========================================
    // 2. DETAIL USER (Admin ATAU Pemilik Akun)
    // ==========================================
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public String getUserDetail(@PathVariable Long id, Model model, Authentication authentication) {
        UserModel user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        if (isAdministrator(authentication) || isOwner(authentication, user)) {
            model.addAttribute("user", user);
            return "UserView/profile";
        } else {
            throw new AccessDeniedException("Anda tidak berhak melihat profil orang lain.");
        }
    }

    // ==========================================
    // 3. CREATE FORM (Hanya Administrator)
    // ==========================================
    @PreAuthorize("hasAuthority('Administrator','ADMINISTRATOR')")
    @GetMapping("/create")
    public String showCreateForm(Model model){
        List<CompanyModel> companies = companyService.getAllCompanyData();
        model.addAttribute("user", new UserModel());
        model.addAttribute("cspv", companySupervisorService.getCompanySupervisor());
        model.addAttribute("companies", companies);
        return "UserView/create-form";
    }

    @PreAuthorize("hasAuthority('Administrator','ADMINISTRATOR')")
    @PostMapping
    public String saveUser(@ModelAttribute UserModel user,
                           // Parameter Student
                           @RequestParam(required = false) String studentNumber,
                           @RequestParam(required = false) String studentPhone,
                           @RequestParam(required = false) String studentName,
                           @RequestParam(required = false) String studentAddress,
                           @RequestParam(required = false) String studentMajor,
                           @RequestParam(required = false) String studentClass,
                           // Parameter School Spv
                           @RequestParam(required = false) String employeeIdNumber,
                           @RequestParam(required = false) String schoolPhone,
                           @RequestParam(required = false) String schoolSupervisorName,
                           @RequestParam(required = false) String schoolName,
                           @RequestParam(required = false) String schoolPosition,
                           @RequestParam(required = false) String schoolSubject,
                           // Parameter Company Spv
                           @RequestParam(required = false) String companyName,
                           @RequestParam(required = false) String jobTitle,
                           @RequestParam(required = false) String companySupervisorName,
                           @RequestParam(required = false) String companySupervisorPhone,
                           // Parameter Admin
                           @RequestParam(required = false) String adminFullName,
                           @RequestParam(required = false) String adminPhone,
                           @RequestParam(required = false) String adminDepartment) {

        userService.createNewUser(
                user,
                studentNumber, studentPhone, studentName, studentAddress, studentMajor, studentClass,
                employeeIdNumber, schoolPhone, schoolSupervisorName, schoolName, schoolPosition, schoolSubject,
                companyName, jobTitle, companySupervisorName, companySupervisorPhone,
                adminFullName, adminPhone, adminDepartment
        );

        return "redirect:/users";
    }

    // ==========================================
    // 4. EDIT FORM (Admin ATAU Pemilik Akun)
    // ==========================================
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable Long id, Model model, Authentication authentication){
        List<CompanyModel> companies = companyService.getAllCompanyData();
        UserModel user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (isAdministrator(authentication) || isOwner(authentication, user)) {
            model.addAttribute("user", user);
            model.addAttribute("roles", UserModel.Role.values());
            model.addAttribute("companies", companies);
            return "UserView/edit-form";
        } else {
            throw new AccessDeniedException("Anda tidak berhak mengedit user ini.");
        }
    }

    // ==========================================
    // 5. UPDATE ACTION (PUT)
    // ==========================================
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute UserModel user,
                             Authentication authentication,
                             // Menerima Input Generik dari View
                             @RequestParam(required = false) String genericFullName,
                             @RequestParam(required = false) String genericPhone,
                             @RequestParam(required = false) String genericAddress,
                             @RequestParam(required = false) String genericIdentityNumber) {

        // Validasi Permission
        UserModel existingUser = userService.getUserById(id).orElseThrow();

        if (!isAdministrator(authentication) && !isOwner(authentication, existingUser)) {
            throw new AccessDeniedException("Tidak ada izin update.");
        }

        // Jika bukan Admin, paksa Role tetap sama dengan yang ada di DB (cegah Student jadi Admin)
        if (!isAdministrator(authentication)) {
            user.setRole(existingUser.getRole());
        }

        // Panggil Service
        userService.updateUser(id, user, genericFullName, genericPhone, genericAddress, genericIdentityNumber);

        // Redirect
        if (isAdministrator(authentication)) {
            return "redirect:/users";
        } else {
            return "redirect:/users/" + id;
        }
    }

    // ==========================================
    // 6. DELETE (Hanya Administrator)
    // ==========================================
    @PreAuthorize("hasAuthority('Administrator','ADMINISTRATOR')")
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return "redirect:/users";
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    private boolean isAdministrator(Authentication authentication) {
     
        return authentication.getAuthorities().stream()
                .map(a -> a.getAuthority().toUpperCase())
                .anyMatch(role -> role.equals("ADMINISTRATOR") || role.equals("ROLE_ADMINISTRATOR") || role.equals("ADMIN"));
    }

    private boolean isOwner(Authentication authentication, UserModel targetUser) {
        if (targetUser == null || targetUser.getUsername() == null) return false;
        return authentication.getName().equals(targetUser.getUsername());
    }
    
}