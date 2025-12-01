package com.sistem.monitoring.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import com.sistem.monitoring.models.AttendanceModel;
import com.sistem.monitoring.models.PlacementModel;
import com.sistem.monitoring.models.SchoolSupervisorModel;
import com.sistem.monitoring.models.UserModel;
import com.sistem.monitoring.services.AttendanceService;
import com.sistem.monitoring.services.PlacementService;
import com.sistem.monitoring.services.SchoolSupervisorService;
import com.sistem.monitoring.services.UserService;

@Controller
@RequestMapping("/attendances")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private UserService userService;

    @Autowired
    private PlacementService placementService;

    @Autowired
    private SchoolSupervisorService schoolSupervisorService;

    private final String UPLOAD_DIR = "src/main/resources/static/uploads/absensi/";

    // ===========================================================
    // 1. FILTER LIST ABSENSI (ADMIN / STUDENT / SUPERVISOR)
    // ===========================================================
    @GetMapping
    public String listAttendance(Model model, Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();

        // Ambil user login
        UserModel user = userService.getAllUser().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        // default
        boolean isAdmin = false;
        boolean isSupervisor = false;
        boolean isStudent = false;

        List<AttendanceModel> attendances = List.of(); // empty default

        // 1) ADMIN → lihat semua
        if (user.getRole() == UserModel.Role.Administrator) {
            isAdmin = true;
            attendances = attendanceService.getAllAttendances();
        }
        // 2) STUDENT → absensinya sendiri
        else if (user.getRole() == UserModel.Role.Student) {
            isStudent = true;
            if (user.getStudent() != null) {
                Long studentId = user.getStudent().getStudentId();
                attendances = attendanceService.getByStudentId(studentId);
            } else {
                attendances = List.of();
            }
        }
        // 3) SCHOOL SUPERVISOR → absensi siswa bimbingan dari PLACEMENT
        else {
            var maybeSupervisor = schoolSupervisorService.findByUserUsername(username);
            if (maybeSupervisor.isPresent()) {
                isSupervisor = true;
                SchoolSupervisorModel supervisor = maybeSupervisor.get();

                List<PlacementModel> placements = placementService
                        .getBySchoolSupervisorId(supervisor.getsSupervisorId());

                List<Long> studentIds = placements.stream()
                        .map(p -> p.getStudent() != null ? p.getStudent().getStudentId() : null)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList());

                attendances = attendanceService.getByStudentIds(studentIds);
            } else {
                // selain admin/student/supervisor -> forbidden
                model.addAttribute("isAdmin", false);
                model.addAttribute("isSupervisor", false);
                model.addAttribute("isStudent", false);
                return "error/403";
            }
        }

        // set model attributes consistent with template
        model.addAttribute("attend", attendances); // <- template expects ${attend}
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isSupervisor", isSupervisor);
        model.addAttribute("isStudent", isStudent);
        model.addAttribute("currentUserName", username);

        return "AttendanceView/index";
    }

    // ===========================================================
    // 2. FORM CREATE (AUTO SET PLACEMENT UNTUK STUDENT)
    // ===========================================================
    @GetMapping("/create")
    public String showFormCreate(Model model, Principal principal) {

        AttendanceModel attend = new AttendanceModel();

        if (principal != null) {
            String username = principal.getName();

            // Cari user login
            UserModel user = userService.getAllUser().stream()
                    .filter(u -> u.getUsername().equals(username))
                    .findFirst().orElse(null);

            if (user != null && user.getRole() == UserModel.Role.Student && user.getStudent() != null) {

                Long studentId = user.getStudent().getStudentId();

                PlacementModel placement = placementService.getPlacementByStudentId(studentId)
                        .orElse(null);

                if (placement != null) {
                    attend.setPlacement(placement);
                }
            }
        }

        model.addAttribute("attend", attend);
        return "AttendanceView/create-form";
    }

    // ===========================================================
    // 3. SIMPAN DATA ABSENSI + FOTO
    // ===========================================================
    @PostMapping
    public String saveData(@ModelAttribute AttendanceModel attend,
            @RequestParam("checkInPhoto") MultipartFile file,
            Principal principal) {

        // --- pastikan user login ---
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();

        // Cari user entity
        UserModel user = userService.getAllUser().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        // --- upload foto (tetap seperti sebelumnya) ---
        if (file != null && !file.isEmpty()) {
            try {
                String fileName = UUID.randomUUID().toString() + "_" +
                        StringUtils.cleanPath(file.getOriginalFilename());

                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath))
                    Files.createDirectories(uploadPath);
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                attend.setCheckInPhotoUrl("/uploads/absensi/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // --- set createdBy (siapa yang membuat record) ---
        attend.setCreatedBy(user);

        // --- set placement & student ---
        // Kasus ideal: jika form sudah mengirim placement (attend.getPlacement()
        // terisi), gunakan itu.
        // Jika tidak (student mengirim dari form create yang di-prefill), kita dapat
        // cari placement berdasarkan student dari user yang login.
        if (attend.getPlacement() == null || attend.getPlacement().getPlacementId() == null) {
            // jika pembuat adalah student, otomatis ambil placement miliknya
            if (user.getRole() == UserModel.Role.Student && user.getStudent() != null) {
                Long studentId = user.getStudent().getStudentId();
                PlacementModel p = placementService.getPlacementByStudentId(studentId).orElse(null);
                if (p != null) {
                    attend.setPlacement(p);
                    attend.setStudent(p.getStudent());
                }
            }
        } else {
            // jika ada placement terisi (misal admin memilih placement di form), pastikan
            // juga set student dari placement
            PlacementModel p = placementService.getPlacementById(attend.getPlacement().getPlacementId())
                    .orElse(null);
            if (p != null) {
                attend.setPlacement(p);
                attend.setStudent(p.getStudent());
            }
        }

        // fallback: jika masih belum ada student, coba gunakan createdBy jika itu
        // student
        if (attend.getStudent() == null && user.getRole() == UserModel.Role.Student && user.getStudent() != null) {
            attend.setStudent(user.getStudent());
        }

        // --- optional: set tanggal/waktu jika service belum handle ---
        // attend.setDate(LocalDate.now()); // kalau model pakai LocalDate
        // attend.setCheckInTime(LocalDateTime.now()); // sesuai tipe di model

        // --- simpan ---
        attendanceService.createAttendance(attend);

        return "redirect:/attendances";
    }

    // ===========================================================
    // 4. EDIT FORM
    // ===========================================================
    @GetMapping("edit/{id}")
    public String editData(@PathVariable Long id, Model model) {
        AttendanceModel attend = attendanceService.getAttendanceById(id)
                .orElseThrow(() -> new RuntimeException("Attendance not found"));
        model.addAttribute("attend", attend);
        return "AttendanceView/edit-form";
    }

    // ===========================================================
    // 5. UPDATE DATA
    // ===========================================================
    @PutMapping("/{id}")
    public String updateData(@PathVariable Long id, @ModelAttribute AttendanceModel attend) {
        attendanceService.updateAttendance(id, attend);
        return "redirect:/attendances";
    }

    // DETAIL VIEW — GET /attendances/{id}
    @GetMapping("/{id:\\d+}")
    public String showDetail(@PathVariable("id") Long id, Model model, Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();

        // ambil user login (sama cara seperti di listAttendance)
        UserModel user = userService.getAllUser().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        // ambil attendance
        AttendanceModel attend = attendanceService.getAttendanceById(id)
                .orElseThrow(() -> new RuntimeException("Attendance not found"));

        // tentukan role flags (sama logika seperti listAttendance)
        boolean isAdmin = false;
        boolean isSupervisor = false;
        boolean isStudent = false;

        if (user.getRole() == UserModel.Role.Administrator) {
            isAdmin = true;
        } else if (user.getRole() == UserModel.Role.Student) {
            isStudent = true;
        } else {
            var maybeSupervisor = schoolSupervisorService.findByUserUsername(username);
            if (maybeSupervisor.isPresent()) {
                isSupervisor = true;
            }
        }

        // optional: cek authorization lebih ketat:
        // - jika student, pastikan attend.getStudent() == user.getStudent()
        // - jika supervisor, pastikan student pada attend termasuk bimbingannya
        // (sesuaikan kebutuhan kebijakan akses Anda)

        model.addAttribute("attend", attend);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isSupervisor", isSupervisor);
        model.addAttribute("isStudent", isStudent);
        model.addAttribute("currentUserName", username);

        return "AttendanceView/detail";
    }

    // APPROVE absensi (set PRESENT)
    @PostMapping("/{id:\\d+}/approve")
    public String approveAttendance(@PathVariable("id") Long id, Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();

        UserModel user = userService.getAllUser().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        // ambil attendance
        AttendanceModel attend = attendanceService.getAttendanceById(id)
                .orElseThrow(() -> new RuntimeException("Attendance not found"));

        // hanya Admin atau Supervisor yang membimbing siswa boleh approve
        boolean isAdmin = user.getRole() == UserModel.Role.Administrator;
        boolean isSupervisor = false;
        if (!isAdmin) {
            var maybeSupervisor = schoolSupervisorService.findByUserUsername(username);
            if (maybeSupervisor.isPresent()) {
                SchoolSupervisorModel sup = maybeSupervisor.get();
                // cek apakah siswa termasuk bimbingannya
                List<PlacementModel> placements = placementService.getBySchoolSupervisorId(sup.getsSupervisorId());
                isSupervisor = placements.stream()
                        .filter(p -> p.getStudent() != null)
                        .anyMatch(p -> Objects.equals(p.getStudent().getStudentId(),
                                attend.getStudent() != null ? attend.getStudent().getStudentId() : null));
            }
        }

        if (!isAdmin && !isSupervisor) {
            // forbidden
            return "error/403";
        }

        // set status jadi PRESENT
        attend.setPresenceStatus(AttendanceModel.Presence.PRESENT);
        // (opsional) catat siapa approve -> bisa pakai createdBy/atau field baru untuk
        // approvedBy
        attendanceService.updateAttendance(id, attend);

        // redirect kembali ke halaman detail
        return "redirect:/attendances/" + id;
    }

    // REJECT absensi (set ABSENT)
    @PostMapping("/{id:\\d+}/reject")
    public String rejectAttendance(@PathVariable("id") Long id, Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();

        UserModel user = userService.getAllUser().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        AttendanceModel attend = attendanceService.getAttendanceById(id)
                .orElseThrow(() -> new RuntimeException("Attendance not found"));

        boolean isAdmin = user.getRole() == UserModel.Role.Administrator;
        boolean isSupervisor = false;
        if (!isAdmin) {
            var maybeSupervisor = schoolSupervisorService.findByUserUsername(username);
            if (maybeSupervisor.isPresent()) {
                SchoolSupervisorModel sup = maybeSupervisor.get();
                List<PlacementModel> placements = placementService.getBySchoolSupervisorId(sup.getsSupervisorId());
                isSupervisor = placements.stream()
                        .filter(p -> p.getStudent() != null)
                        .anyMatch(p -> Objects.equals(p.getStudent().getStudentId(),
                                attend.getStudent() != null ? attend.getStudent().getStudentId() : null));
            }
        }

        if (!isAdmin && !isSupervisor) {
            return "error/403";
        }

        // set status jadi ABSENT
        attend.setPresenceStatus(AttendanceModel.Presence.ABSENT);
        attendanceService.updateAttendance(id, attend);

        return "redirect:/attendances/" + id;
    }

    // ===========================================================
    // 6. DELETE DATA
    // ===========================================================
    @DeleteMapping("/{id}")
    public String deleteData(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return "redirect:/attendances";
    }
}
