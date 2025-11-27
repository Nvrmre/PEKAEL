package com.sistem.monitoring.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
import com.sistem.monitoring.services.SchoolSupervisorService;
import com.sistem.monitoring.services.StudentServices;
import com.sistem.monitoring.services.UserService;

@Controller
@RequestMapping("/report-submissions")
public class ReportSubmissionController {

    private final ReportSubmissionService reportSubmissionService;
    private final PlacementService placementService;
    private final UserService userService;
    private final SchoolSupervisorService schoolSupervisorService;
    private final StudentServices studentServices;

    // Direktori Upload
    private final Path uploadDir = Paths.get("src/main/resources/static/uploads/report-submission/");

    public ReportSubmissionController(ReportSubmissionService reportService,
                                      PlacementService placementService,
                                      UserService userService,
                                      SchoolSupervisorService schoolSupervisorService,
                                      StudentServices studentServices
                                     ) {
        this.reportSubmissionService = reportService;
        this.placementService = placementService;
        this.userService = userService;
        this.schoolSupervisorService = schoolSupervisorService;
        this.studentServices = studentServices;
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
    public String listReports(Model model, Principal principal) {
        String username = principal != null ? principal.getName() : null;
        List<ReportSubmissionModel> reports;

        if (username == null) {
            // anonymous: redirect ke login atau tampil kosong
            return "redirect:/login";
        }

        try {
            // apakah user adalah school supervisor?
            var maybeSupervisor = schoolSupervisorService.findByUserUsername(username);
            if (maybeSupervisor.isPresent()) {
                Long supervisorId = maybeSupervisor.get().getsSupervisorId();

                // ambil placements yang di-assign ke supervisor ini
                List<PlacementModel> placements = placementService.getBySchoolSupervisorId(supervisorId);

                // ekstrak student IDs
                List<Long> studentIds = placements.stream()
                        .map(p -> p.getStudent() != null ? p.getStudent().getStudentId() : null)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList());

                // ambil reports untuk student tersebut AND reports yang dibuat oleh user login
                reports = reportSubmissionService.getByCreatorOrStudents(username, studentIds);

                // set flag di model agar view tahu ini supervisor
                model.addAttribute("isSupervisor", true);
                model.addAttribute("currentUserName", username);
            } else {
                // bukan supervisor: hanya tampilkan report yang dibuat user itu sendiri
                reports = reportSubmissionService.getByCreatorUsername(username);
                model.addAttribute("isSupervisor", false);
                model.addAttribute("currentUserName", username);
            }
        } catch (Exception ex) {
            // fallback: tampilkan hanya milik user
            reports = reportSubmissionService.getByCreatorUsername(username);
            model.addAttribute("isSupervisor", false);
        }

        model.addAttribute("submission", reports);
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
    public String saveReport(
            @ModelAttribute("submission") ReportSubmissionModel submission,
            @RequestParam("fileUpload") MultipartFile fileUpload,
            @RequestParam(value = "studentId", required = false) Long studentId, // opsional: bila admin memilih siswa
            Principal principal) {

        System.out.println("=== MULAI PROSES SAVE REPORT ===");

        if (principal == null) {
            System.out.println("ERROR: Principal is NULL (User belum login atau session habis)");
            return "redirect:/Auth/login";
        }

        String username = principal.getName();
        System.out.println("User Login: " + username);

        // Ambil user login secara langsung (lebih efisien daripada getAllUser stream)
        UserModel user = userService.findByUsername(username).orElse(null); // sesuaikan kalau service-mu beda
        if (user == null) {
            System.out.println("ERROR: User tidak ditemukan di DB untuk username " + username);
            return "redirect:/Auth/login";
        }

        // set createdBy selalu dari user login (PENTING)
        submission.setCreatedBy(user);

        // Jika ada studentId dikirim (misal admin memilih student), pakai itu
        if (studentId != null) {
            var maybeStudent = studentServices.getUserStudentById(studentId);
            if (maybeStudent.isPresent()) {
                submission.setStudent(maybeStudent.get());
                System.out.println("Student di-set dari param studentId: " + studentId);
            } else {
                System.out.println("WARNING: studentId param tidak ditemukan: " + studentId);
            }
        } else {
            // Jika uploader adalah student, set student berdasarkan relasi user -> student (umumnya)
            if (user.getRole() == UserModel.Role.Student && user.getStudent() != null) {
                submission.setStudent(user.getStudent());
                System.out.println("Student di-set dari user.student: " + user.getStudent().getStudentId());
            } else {
                // kalau bukan student dan tidak ada studentId, submission.student bisa tetap null (admin/manual)
                System.out.println("Info: uploader bukan student dan tidak mengirim studentId. submission.student mungkin null.");
            }
        }

        // Set default status jika belum ada
        if (submission.getStatus() == null) {
            submission.setStatus(ReportSubmissionModel.Status.PENDING);
        }

        // Set timestamps
        var now = java.time.LocalDateTime.now();
        if (submission.getCreatedAt() == null) submission.setCreatedAt(now);
        submission.setUpdatedAt(now);

        // Upload file jika ada
        if (fileUpload != null && !fileUpload.isEmpty()) {
            System.out.println("Mulai Upload File: " + fileUpload.getOriginalFilename());
            String filename = System.currentTimeMillis() + "-" + org.springframework.util.StringUtils.cleanPath(fileUpload.getOriginalFilename());
            try {
                java.nio.file.Path dest = uploadDir.resolve(filename).normalize();
                java.nio.file.Files.copy(fileUpload.getInputStream(), dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                submission.setFilePath("/uploads/report-submission/" + filename);
                System.out.println("Upload Berhasil. Path: " + submission.getFilePath());
            } catch (java.io.IOException e) {
                System.out.println("ERROR Upload File: " + e.getMessage());
                e.printStackTrace();
                // kalau mau: return ke form dengan pesan error
            }
        } else {
            System.out.println("INFO: Tidak ada file yang diupload.");
        }

        // Simpan
        try {
            System.out.println("Mencoba menyimpan ke database...");
            reportSubmissionService.createSubmission(submission);
            System.out.println("SUKSES: Data tersimpan di Database!");
        } catch (Exception e) {
            System.out.println("FATAL ERROR SAAT SAVE: " + e.getMessage());
            e.printStackTrace();
            // bisa redirect ke halaman error atau kembalikan ke form
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