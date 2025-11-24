package com.sistem.monitoring.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.sistem.monitoring.models.PlacementModel;
import com.sistem.monitoring.models.ReportSubmissionModel;
import com.sistem.monitoring.models.UserModel;
import com.sistem.monitoring.services.PlacementService;
import com.sistem.monitoring.services.ReportSubmissionService;
import com.sistem.monitoring.services.UserService;

@Controller
@RequestMapping("/report-submissions")
public class ReportSubmissionController {

    private final ReportSubmissionService reportSubmissionService;
    private final PlacementService placementService;
    private final UserService userService;

    // Direktori Upload
    private final Path uploadDir = Paths.get("src/main/resources/static/uploads/report-submission/");

    public ReportSubmissionController(ReportSubmissionService reportService,
            PlacementService placementService,
            UserService userService) {
        this.reportSubmissionService = reportService;
        this.placementService = placementService;
        this.userService = userService;
        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    // ==========================================
    // 1. LIST DATA
    // ==========================================
    @GetMapping
    public String showAllSubmission(Model model) {
        model.addAttribute("submission", reportSubmissionService.getAllSubmission());
        return "ReportSubmissionView/index";
    }

    // ==========================================
    // 2. FORM CREATE
    // ==========================================
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        ReportSubmissionModel report = new ReportSubmissionModel();
        model.addAttribute("submission", report);
        // Placement tidak perlu di-load semua jika auto-detect by student login
        // model.addAttribute("placement", placementService.getAllPlacement());
        return "ReportSubmissionView/create-form";
    }

    // ==========================================
    // 3. SAVE ACTION (UPLOAD + AUTO STUDENT)
    // ==========================================
    @PostMapping
    public String saveReport(@ModelAttribute("submission") ReportSubmissionModel submission,
            @RequestParam("fileUpload") MultipartFile fileUpload,
            Principal principal) {

        System.out.println("=== MULAI PROSES SAVE REPORT ==="); // Jejak 1

        // 1. Cek Apakah User Login?
        if (principal == null) {
            System.out.println("ERROR: Principal is NULL (User belum login atau session habis)");
            return "redirect:/Auth/login";
        }

        String username = principal.getName();
        System.out.println("User Login: " + username); // Jejak 2

        // 2. Cari Data User & Student
        UserModel user = userService.getAllUser().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst().orElse(null);

        // Cek apakah user ditemukan dan dia Student
        if (user != null && user.getRole() == UserModel.Role.Student && user.getStudent() != null) {
            Long studentId = user.getStudent().getStudentId();
            System.out.println("Student ID ditemukan: " + studentId); // Jejak 3

            // 3. Cari Placement (Penempatan)
            PlacementModel myPlacement = placementService.getPlacementByStudentId(studentId)
                    .orElse(null);

            if (myPlacement != null) {
                System.out.println("Placement ditemukan: " + myPlacement.getCompany().getCompanyName()); // Jejak 4

                // SET SISWA KE LAPORAN
                submission.setStudent(myPlacement.getStudent());
            } else {
                System.out.println("BAHAYA: Siswa ini BELUM punya Placement/PKL! Data Student tidak akan masuk.");
                // Opsional: return "redirect:/report-submissions/create?error=no_placement";
            }
        } else {
            System.out.println("INFO: User bukan Student atau Data Student null. (Mungkin Admin input manual?)");
        }

        // 4. Set Status Default
        if (submission.getStatus() == null) {
            submission.setStatus(ReportSubmissionModel.Status.PENDING);
        }

        // 5. Upload File
        if (fileUpload != null && !fileUpload.isEmpty()) {
            System.out.println("Mulai Upload File: " + fileUpload.getOriginalFilename()); // Jejak 5
            String filename = System.currentTimeMillis() + "-"
                    + StringUtils.cleanPath(fileUpload.getOriginalFilename());
            try {
                Path dest = uploadDir.resolve(filename).normalize();
                Files.copy(fileUpload.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

                submission.setFilePath("/uploads/report-submission/" + filename);
                // submission.setFileUrl("/uploads/report-submission/" + filename);
                System.out.println("Upload Berhasil. Path: " + submission.getFilePath());
            } catch (IOException e) {
                System.out.println("ERROR Upload File: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("INFO: Tidak ada file yang diupload.");
        }

        // 6. EKSEKUSI SIMPAN KE DB
        try {
            System.out.println("Mencoba menyimpan ke database..."); // Jejak 6
            reportSubmissionService.createSubmission(submission);
            System.out.println("SUKSES: Data tersimpan di Database!"); // Jejak 7
        } catch (Exception e) {
            System.out.println("FATAL ERROR SAAT SAVE: " + e.getMessage()); // Jejak 8
            e.printStackTrace();
        }

        return "redirect:/report-submissions";
    }

    // ==========================================
    // 4. EDIT FORM
    // ==========================================
    @GetMapping("/edit/{id}")
    public String editData(@PathVariable Long id, Model model) {
        ReportSubmissionModel report = reportSubmissionService.getSubmissionById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        model.addAttribute("submission", report);
        return "ReportSubmissionView/edit-form";
    }

    // ==========================================
    // 5. UPDATE ACTION
    // ==========================================
    @PutMapping("/{id}") // PERBAIKAN: Tambahkan /{id}
    public String updateData(@PathVariable Long id, @ModelAttribute ReportSubmissionModel report) {
        reportSubmissionService.updateReportSubmission(id, report);
        return "redirect:/report-submissions";
    }

    // ==========================================
    // 6. DELETE ACTION
    // ==========================================
    @DeleteMapping("/{id}")
    public String deleteData(@PathVariable Long id) {
        reportSubmissionService.deleteReport(id);
        return "redirect:/report-submissions";
    }

    // ==========================================
    // 7. DETAIL VIEW
    // ==========================================
    @GetMapping("/{id}")
    public String viewReportDetail(@PathVariable Long id, Model model, Principal principal) {
        ReportSubmissionModel report = reportSubmissionService.getSubmissionById(id)
                .orElseThrow(() -> new RuntimeException("Laporan tidak ditemukan"));

        model.addAttribute("report", report);
        return "ReportSubmissionView/detail";
    }

    // ==========================================
    // 8. APPROVE / REJECT (Admin & Guru)
    // ==========================================
    @PostMapping("/{id}/update-status")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'SCHOOL_SUPERVISOR', 'ROLE_ADMINISTRATOR', 'ROLE_SCHOOL_SUPERVISOR')")
    public String updateReportStatus(@PathVariable Long id, 
                                     @RequestParam("status") String statusStr) {
        
        // 1. Ambil data laporan yang ada
        ReportSubmissionModel report = reportSubmissionService.getSubmissionById(id)
                .orElseThrow(() -> new RuntimeException("Laporan tidak ditemukan"));

        try {
            // 2. Ubah String dari HTML menjadi Enum
            ReportSubmissionModel.Status newStatus = ReportSubmissionModel.Status.valueOf(statusStr);
            
            // 3. Set status baru
            report.setStatus(newStatus);
            
            // 4. Simpan perubahan (Gunakan method create/save yang sudah ada di service)
            reportSubmissionService.createSubmission(report); 
            
        } catch (IllegalArgumentException e) {
            System.out.println("Eror: Status " + statusStr + " tidak dikenali.");
        }

        // 5. Kembali ke halaman detail
        return "redirect:/report-submissions/" + id;
    }
}