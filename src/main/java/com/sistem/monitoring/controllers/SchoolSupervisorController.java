package com.sistem.monitoring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.sistem.monitoring.models.SchoolSupervisorModel;
import com.sistem.monitoring.models.UserModel;
import com.sistem.monitoring.services.SchoolSupervisorService;
import com.sistem.monitoring.services.UserService;

import java.util.Optional;

@Controller
@RequestMapping("/sch-spv")
public class SchoolSupervisorController {

    @Autowired
    private SchoolSupervisorService schoolSupervisorService;

    @Autowired
    private UserService userService;

    // ==========================================
    // 1. LIST DATA (Hanya Administrator)
    // ==========================================
    @PreAuthorize("hasAuthority('Administrator')")
    @GetMapping
    public String getAllDataFromSchoolSupervisor(Model model){
        model.addAttribute("sspv", schoolSupervisorService.getSchoolSupervisor());
        return "SchoolSupervisorView/index";
    }

    // ==========================================
    // 2. DETAIL PROFIL (Admin / Pemilik Akun)
    // ==========================================
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public String getDataSupervisorById(@PathVariable Long id, Model model, Authentication authentication){
        SchoolSupervisorModel spv = schoolSupervisorService.getSchoolsupervisorById(id)
                .orElseThrow(() -> new RuntimeException("Supervisor not found"));

        // Security Check: Admin atau Pemilik Akun
        if (isAdministrator(authentication) || isOwner(authentication, spv)) {
            model.addAttribute("sspv", spv);
            return "SchoolSupervisorView/profile";
        } else {
            throw new AccessDeniedException("Anda tidak berhak melihat profil ini.");
        }
    }

    // ==========================================
    // 3. CREATE FORM (Hanya Administrator)
    // ==========================================
    @PreAuthorize("hasAuthority('Administrator')")
    @GetMapping("/create")
    public String showCreateForm(Model model){
        SchoolSupervisorModel spv = new SchoolSupervisorModel();
        spv.setUser(new UserModel()); // Inisialisasi User agar tidak null di form
        model.addAttribute("sspv", spv);
        return "SchoolSupervisorView/create-form";
    }

    // ==========================================
    // 4. CREATE ACTION (Integrasi UserService)
    // ==========================================
    @PreAuthorize("hasAuthority('Administrator')")
    @PostMapping
    public String saveSupervisorData(@ModelAttribute SchoolSupervisorModel spv){
        UserModel user = spv.getUser();
        if (user == null) user = new UserModel();


        user.setRole(UserModel.Role.School_Supervisor);


        userService.createNewUser(
                user,
                null, null, null, null, null, null,

                spv.getEmployeeIdNumber(),         // NIP
                spv.getSchoolSupervisorPhone(),    // No HP
                spv.getSchoolSupervisorFullName(), // Nama Lengkap
                spv.getSchoolName(),               // Nama Sekolah
                spv.getSchoolSupervisorPosition(), // Jabatan
                spv.getSchoolSupervisorSubject(),  // Mata Pelajaran

                null, null, null, null,

                null, null, null
        );

        return "redirect:/sch-spv";
    }

    // ==========================================
    // 5. EDIT FORM (Admin / Pemilik Akun)
    // ==========================================
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/edit/{id}")
    public String editDataSchoolSupervisor(@PathVariable Long id, Model model, Authentication authentication){
        SchoolSupervisorModel spv = schoolSupervisorService.getSchoolsupervisorById(id)
                .orElseThrow(()-> new RuntimeException("User Not found to edit"));

        if (isAdministrator(authentication) || isOwner(authentication, spv)) {
            model.addAttribute("sspv", spv);
            return "SchoolSupervisorView/edit-form";
        } else {
            throw new AccessDeniedException("Anda tidak berhak mengedit data ini.");
        }
    }

    // ==========================================
    // 6. UPDATE ACTION (Integrasi UserService)
    // ==========================================
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public String updateSupervisor(@PathVariable Long id,
                                   @ModelAttribute SchoolSupervisorModel spvInput,
                                   Authentication authentication){

        SchoolSupervisorModel existingSpv = schoolSupervisorService.getSchoolsupervisorById(id)
                .orElseThrow(() -> new RuntimeException("Supervisor not found"));


        if (!isAdministrator(authentication) && !isOwner(authentication, existingSpv)) {
            throw new AccessDeniedException("Tidak ada izin update.");
        }
        Long userId = existingSpv.getUser().getUserId();
        UserModel userWrapper = new UserModel();
        userWrapper.setRole(UserModel.Role.School_Supervisor);
        if (spvInput.getUser() != null) {
            userWrapper.setUsername(spvInput.getUser().getUsername());
            userWrapper.setEmail(spvInput.getUser().getEmail());
            userWrapper.setPassword(spvInput.getUser().getPassword());
        }
        userService.updateUser(
                userId,
                userWrapper,
                spvInput.getSchoolSupervisorFullName(),
                spvInput.getSchoolSupervisorPhone(),
                spvInput.getSchoolSupervisorAddress(),
                spvInput.getEmployeeIdNumber()
        );

        if (isAdministrator(authentication)) {
            return "redirect:/sch-spv";
        } else {
            return "redirect:/sch-spv/" + id;
        }
    }

    // ==========================================
    // 7. DELETE (Hanya Administrator)
    // ==========================================
    @PreAuthorize("hasAuthority('Administrator')")
    @DeleteMapping("/{id}")
    public String deleteSchoolSupervisor(@PathVariable Long id){


        SchoolSupervisorModel spv = schoolSupervisorService.getSchoolsupervisorById(id).orElseThrow();
        userService.deleteUser(spv.getUser().getUserId());

        return "redirect:/sch-spv";
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    private boolean isAdministrator(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(a -> a.getAuthority().toUpperCase())
                .anyMatch(role -> role.equals("ADMINISTRATOR") || role.equals("ROLE_ADMINISTRATOR"));
    }

    private boolean isOwner(Authentication authentication, SchoolSupervisorModel spv) {
        if (spv == null || spv.getUser() == null) return false;
        return authentication.getName().equals(spv.getUser().getUsername());
    }
}