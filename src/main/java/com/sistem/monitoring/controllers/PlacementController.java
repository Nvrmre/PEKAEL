package com.sistem.monitoring.controllers;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.sistem.monitoring.models.CompanyModel;
import com.sistem.monitoring.models.CompanySupervisorModel;
import com.sistem.monitoring.models.PlacementModel;
import com.sistem.monitoring.models.SchoolSupervisorModel;
import com.sistem.monitoring.models.StudentModel;
import com.sistem.monitoring.models.UserModel;
import com.sistem.monitoring.models.PlacementModel.Status;
import com.sistem.monitoring.services.CompanyService;
import com.sistem.monitoring.services.CompanySupervisorService;
import com.sistem.monitoring.services.PlacementService;
import com.sistem.monitoring.services.SchoolSupervisorService;
import com.sistem.monitoring.services.StudentServices;

@Controller
@RequestMapping("/placements")
public class PlacementController {

    private final PlacementService placementService;
    private final StudentServices studentServices;
    private final CompanyService companyService;
    private final SchoolSupervisorService schoolSupervisorService;
    private final CompanySupervisorService companySupervisorService;

    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public PlacementController(
            PlacementService placementService,
            StudentServices studentServices,
            CompanyService companyService,
            SchoolSupervisorService schoolSupervisorService,
            CompanySupervisorService companySupervisorService) {
        this.placementService = placementService;
        this.studentServices = studentServices;
        this.companyService = companyService;
        this.schoolSupervisorService = schoolSupervisorService;
        this.companySupervisorService = companySupervisorService;
    }

    @GetMapping
    public String listPlacements(Model model, Principal principal) {
        List<PlacementModel> list;
        boolean isSupervisor = false;
        String currentUserName = "";

        try {
            if (principal != null) {
                String username = principal.getName(); // username/email dari Spring Security

                // user ini adalah School Supervisor
                Optional<SchoolSupervisorModel> maybeSupervisor = schoolSupervisorService.findByUserUsername(username);

                if (maybeSupervisor.isPresent()) {
                    SchoolSupervisorModel supervisor = maybeSupervisor.get();
                    // tampilkan hanya placement milik supervisor ini
                    list = placementService.getBySchoolSupervisorId(supervisor.getsSupervisorId());
                    isSupervisor = true;
                    // prefer full name jika tersedia, fallback ke user.username
                    if (supervisor.getSchoolSupervisorFullName() != null && !supervisor.getSchoolSupervisorFullName().isBlank()) {
                        currentUserName = supervisor.getSchoolSupervisorFullName();
                    } else if (supervisor.getUser() != null) {
                        currentUserName = supervisor.getUser().getUsername();
                    } else {
                        currentUserName = username;
                    }
                } else {
                    // bukan guru -> tampilkan semua (atau ubah sesuai kebijakan: redirect/forbidden)
                    list = placementService.getAllPlacement();
                    currentUserName = username;
                }
            } else {
                // anonymous user -> tampil semua atau redirect ke login
                list = placementService.getAllPlacement();
            }
        } catch (Exception ex) {
            // fallback: tampil semua agar tidak crash
            list = placementService.getAllPlacement();
        }

        model.addAttribute("placements", list);
        model.addAttribute("isSupervisor", isSupervisor);
        model.addAttribute("currentUserName", currentUserName);
        return "PlacementView/index";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        List<StudentModel> dummy = studentServices.getAllUserStudent();
        if (dummy.isEmpty()) {
            // tambahkan data dummy buat ngetes tampilan
            StudentModel s = new StudentModel();
            s.setStudentId(1L);
            UserModel u = new UserModel();
            u.setUsername("Dummy Student");
            s.setUser(u);
            dummy.add(s);
        }
        model.addAttribute("placement", new PlacementModel());
        model.addAttribute("students", studentServices.getAllUserStudent());
        model.addAttribute("companies", companyService.getAllCompanyData());
        model.addAttribute("schoolSupervisors", schoolSupervisorService.getSchoolSupervisor());
        model.addAttribute("companySupervisors", companySupervisorService.getCompanySupervisor());
        model.addAttribute("statuses", Status.values());

        return "PlacementView/create-form";
    }

    @PostMapping
    public String savePlacement(
            @ModelAttribute("placement") PlacementModel placement,
            @RequestParam("studentId") Long studentId,
            @RequestParam("companyId") Long companyId,
            @RequestParam(value = "schoolSupervisorId", required = false) Long schoolSupervisorId,
            @RequestParam(value = "companySupervisorId", required = false) Long companySupervisorId,
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr,
            @RequestParam("status") String statusStr,
            Model model) {

        StudentModel student = studentServices.getUserStudentById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));
        CompanyModel company = companyService.getCompanyById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found: " + companyId));

        placement.setStudent(student);
        placement.setCompany(company);

        if (schoolSupervisorId != null) {
            SchoolSupervisorModel sspv = schoolSupervisorService.getSchoolsupervisorById(schoolSupervisorId)
                    .orElseThrow(() -> new RuntimeException("SchoolSupervisor not found: " + schoolSupervisorId));
            placement.setSchoolSupervisor(sspv);
        } else {
            placement.setSchoolSupervisor(null);
        }

        if (companySupervisorId != null) {
            CompanySupervisorModel cspv = companySupervisorService.getCompanysupervisorById(companySupervisorId)
                    .orElseThrow(() -> new RuntimeException("CompanySupervisor not found: " + companySupervisorId));
            placement.setCompanySupervisor(cspv);
        } else {
            placement.setCompanySupervisor(null);
        }

        try {
            LocalDateTime start = LocalDateTime.parse(startDateStr, DT_FORMAT);
            LocalDateTime end = LocalDateTime.parse(endDateStr, DT_FORMAT);
            placement.setStartDate(start);
            placement.setEndDate(end);
        } catch (Exception ex) {
            throw new RuntimeException("Invalid date format. Expected: yyyy-MM-dd'T'HH:mm", ex);
        }

        try {
            placement.setStatus(Status.valueOf(statusStr));
        } catch (Exception ex) {
            placement.setStatus(Status.PENDING);
        }

        placementService.createPlacement(placement);
        return "redirect:/placements";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        PlacementModel placement = placementService.getPlacementById(id)
                .orElseThrow(() -> new RuntimeException("Placement not found: " + id));
        model.addAttribute("placement", placement);
        model.addAttribute("students", studentServices.getAllUserStudent());
        model.addAttribute("companies", companyService.getAllCompanyData());
        model.addAttribute("schoolSupervisors", schoolSupervisorService.getSchoolSupervisor());
        model.addAttribute("companySupervisors", companySupervisorService.getCompanySupervisor());
        model.addAttribute("statuses", Status.values());
        return "PlacementView/edit-form";
    }

    // HANDLE UPDATE
    @PutMapping("/{id}")
    public String updatePlacement(
            @PathVariable Long id,
            @ModelAttribute("placement") PlacementModel formPlacement,
            @RequestParam("studentId") Long studentId,
            @RequestParam("companyId") Long companyId,
            @RequestParam(value = "schoolSupervisorId", required = false) Long schoolSupervisorId,
            @RequestParam(value = "companySupervisorId", required = false) Long companySupervisorId,
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr,
            @RequestParam("status") String statusStr) {

        // pastikan student & company valid
        StudentModel student = studentServices.getUserStudentById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));
        CompanyModel company = companyService.getCompanyById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found: " + companyId));

        formPlacement.setStudent(student);
        formPlacement.setCompany(company);

        if (schoolSupervisorId != null) {
            SchoolSupervisorModel sspv = schoolSupervisorService.getSchoolsupervisorById(schoolSupervisorId)
                    .orElseThrow(() -> new RuntimeException("SchoolSupervisor not found: " + schoolSupervisorId));
            formPlacement.setSchoolSupervisor(sspv);
        } else {
            formPlacement.setSchoolSupervisor(null);
        }

        if (companySupervisorId != null) {
            CompanySupervisorModel cspv = companySupervisorService.getCompanysupervisorById(companySupervisorId)
                    .orElseThrow(() -> new RuntimeException("CompanySupervisor not found: " + companySupervisorId));
            formPlacement.setCompanySupervisor(cspv);
        } else {
            formPlacement.setCompanySupervisor(null);
        }

        try {
            LocalDateTime start = LocalDateTime.parse(startDateStr, DT_FORMAT);
            LocalDateTime end = LocalDateTime.parse(endDateStr, DT_FORMAT);
            formPlacement.setStartDate(start);
            formPlacement.setEndDate(end);
        } catch (Exception ex) {
            throw new RuntimeException("Invalid date format. Expected: yyyy-MM-dd'T'HH:mm", ex);
        }

        try {
            formPlacement.setStatus(Status.valueOf(statusStr));
        } catch (Exception ex) {
            // keep existing or set default
            formPlacement.setStatus(Status.PENDING);
        }

        // gunakan service update (service akan mencari existing entity dan menyimpan perubahan)
        placementService.updatePlacement(id, formPlacement);
        return "redirect:/placements";
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String deletePlacement(@PathVariable Long id) {
        placementService.deletePlacement(id);
        return "redirect:/placements";
    }

    @GetMapping("/detail/{id}")
    public String showDetail(@PathVariable Long id, Model model) {
        try {
            PlacementModel placement = placementService.getPlacementById(id)
                    .orElseThrow(() -> new RuntimeException("Placement not found"));
            model.addAttribute("placement", placement);
            return "PlacementView/detail";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error/custom-error";
        }
    }

}
