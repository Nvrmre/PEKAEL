package com.sistem.monitoring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.sistem.monitoring.models.CompanyModel;
import com.sistem.monitoring.models.CompanySupervisorModel;
import com.sistem.monitoring.models.UserModel;
import com.sistem.monitoring.services.CompanyService;
import com.sistem.monitoring.services.CompanySupervisorService;
import com.sistem.monitoring.services.UserService;

@Controller
@RequestMapping("/comp-spv")
public class CompanySupervisorController {

    @Autowired
    private CompanySupervisorService companySupervisorService;

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    // ==========================================
    // 1. LIST DATA (Hanya Administrator)
    // ==========================================
    @PreAuthorize("hasAuthority('Administrator')")
    @GetMapping
    public String getAllDataFromCompanySupervisor(Model model){
        model.addAttribute("cspv", companySupervisorService.getCompanySupervisor());
        return "CompanySupervisorView/index";
    }

    // ==========================================
    // 2. DETAIL PROFIL (Admin / Pemilik Akun)
    // ==========================================
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public String getCompanySupervisorById(@PathVariable Long id, Model model, Authentication authentication){
        CompanySupervisorModel spv = companySupervisorService.getCompanysupervisorById(id)
                .orElseThrow(() -> new RuntimeException("Supervisor not found"));
        if (isAdministrator(authentication) || isOwner(authentication, spv)) {
            model.addAttribute("cspv", spv);
            return "CompanySupervisorView/profile";
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
        CompanySupervisorModel spv = new CompanySupervisorModel();
        spv.setUser(new UserModel()); // Init user agar form tidak error
        model.addAttribute("cspv", spv);
        model.addAttribute("companies", companyService.getAllCompanyData()); // List Perusahaan untuk Dropdown
        return "CompanySupervisorView/create-form";
    }

    // ==========================================
    // 4. CREATE ACTION (Integrasi UserService)
    // ==========================================
    @PreAuthorize("hasAuthority('Administrator')")
    @PostMapping
    public String saveSupervisorData(@ModelAttribute CompanySupervisorModel spv,
                                     @RequestParam("companyId") Long companyId ){

        UserModel user = spv.getUser();
        if (user == null) user = new UserModel();

        user.setRole(UserModel.Role.Company_Supervisor);


        UserModel savedUser = userService.createNewUser(
                user,

                null, null, null, null, null, null,
                null, null, null, null, null, null,

                null,
                spv.getJobTitle(),
                spv.getCompanySupervisorFullName(),
                spv.getCompanySupervisorPhone(),
                null, null, null
        );

        CompanySupervisorModel savedSpv = savedUser.getCompanySupervisor();
        if (savedSpv != null && companyId != null) {
            CompanyModel company = companyService.getCompanyById(companyId)
                    .orElseThrow(()-> new RuntimeException("Company not found"));

            savedSpv.setCompany(company);
            companySupervisorService.updateCompanySupervisor(savedSpv.getcSupervisorId(), savedSpv, companyId);
        }

        return "redirect:/comp-spv";
    }

    // ==========================================
    // 5. EDIT FORM (Admin / Pemilik Akun)
    // ==========================================
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/edit/{id}")
    public String editDataCompanySupervisor(@PathVariable Long id, Model model, Authentication authentication){
        CompanySupervisorModel spv = companySupervisorService.getCompanysupervisorById(id)
                .orElseThrow(()-> new RuntimeException("Supervisor not found"));

        if (isAdministrator(authentication) || isOwner(authentication, spv)) {
            model.addAttribute("cspv", spv);
            model.addAttribute("companies", companyService.getAllCompanyData());
            return "CompanySupervisorView/edit-form";
        } else {
            throw new AccessDeniedException("Anda tidak berhak mengedit data ini.");
        }
    }

    // ==========================================
    // 6. UPDATE ACTION (Integrasi UserService)
    // ==========================================
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public String updateSupervisor(
            @PathVariable Long id,
            @ModelAttribute("cspv") CompanySupervisorModel spvInput,
            @RequestParam("companyId") Long companyId,
            Authentication authentication) {


        CompanySupervisorModel existingSpv = companySupervisorService.getCompanysupervisorById(id)
                .orElseThrow(() -> new RuntimeException("Supervisor Not Found"));

        if (!isAdministrator(authentication) && !isOwner(authentication, existingSpv)) {
            throw new AccessDeniedException("Tidak ada izin update.");
        }

        Long userId = existingSpv.getUser().getUserId();

        UserModel userWrapper = new UserModel();
        userWrapper.setRole(UserModel.Role.Company_Supervisor);
        if (spvInput.getUser() != null) {
            userWrapper.setUsername(spvInput.getUser().getUsername());
            userWrapper.setEmail(spvInput.getUser().getEmail());
            userWrapper.setPassword(spvInput.getUser().getPassword());
        }


        userService.updateUser(
                userId,
                userWrapper,
                spvInput.getCompanySupervisorFullName(),
                spvInput.getCompanySupervisorPhone(),
                null,
                null
        );


        existingSpv.setJobTitle(spvInput.getJobTitle());
        companySupervisorService.updateCompanySupervisor(id, existingSpv, companyId);

        if (isAdministrator(authentication)) {
            return "redirect:/comp-spv";
        } else {
            return "redirect:/comp-spv/" + id;
        }
    }

    // ==========================================
    // 7. DELETE (Hanya Administrator)
    // ==========================================
    @PreAuthorize("hasAuthority('Administrator')")
    @DeleteMapping("/{id}")
    public String deleteCompanySupervisor(@PathVariable Long id){
        CompanySupervisorModel spv = companySupervisorService.getCompanysupervisorById(id).orElseThrow();
        userService.deleteUser(spv.getUser().getUserId());

        return "redirect:/comp-spv";
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    private boolean isAdministrator(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(a -> a.getAuthority().toUpperCase())
                .anyMatch(role -> role.equals("ADMINISTRATOR") || role.equals("ROLE_ADMINISTRATOR"));
    }

    private boolean isOwner(Authentication authentication, CompanySupervisorModel spv) {
        if (spv == null || spv.getUser() == null) return false;
        return authentication.getName().equals(spv.getUser().getUsername());
    }
}