package com.sistem.monitoring.controllers;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sistem.monitoring.models.PlacementModel;
import com.sistem.monitoring.models.UserModel;
import com.sistem.monitoring.services.AttendanceService;
import com.sistem.monitoring.services.CompanyService;
import com.sistem.monitoring.services.CompanySupervisorService;
import com.sistem.monitoring.services.DailyJournalService;
// import com.sistem.monitoring.services.GradeService;
import com.sistem.monitoring.services.PlacementService;
import com.sistem.monitoring.services.ReportSubmissionService;
import com.sistem.monitoring.services.SchoolSupervisorService;
import com.sistem.monitoring.services.StudentServices;
import com.sistem.monitoring.services.UserService;

@Controller
public class HomeController {

    private final UserService userService;
    private final StudentServices studentServices;
    private final CompanySupervisorService companySupervisorService;
    private final SchoolSupervisorService schoolSupervisorService;
    private final PlacementService placementService;
    private final DailyJournalService dailyJournalService;
    private final CompanyService companyService;
    // private final GradeService gradeService;
    private final ReportSubmissionService reportSubmissionService;
    private final AttendanceService attendanceService;

    public HomeController(
            UserService userService,
            StudentServices studentServices,
            CompanySupervisorService companySupervisorService,
            SchoolSupervisorService schoolSupervisorService,
            PlacementService placementService,
            DailyJournalService dailyJournalService,
            CompanyService companyService,
            // GradeService gradeService,
            ReportSubmissionService reportSubmissionService,
            AttendanceService attendanceService) {
        this.userService = userService;
        this.studentServices = studentServices;
        this.companySupervisorService = companySupervisorService;
        this.schoolSupervisorService = schoolSupervisorService;
        this.placementService = placementService;
        this.dailyJournalService = dailyJournalService;
        this.companyService = companyService;
        // this.gradeService = gradeService;
        this.reportSubmissionService = reportSubmissionService;
        this.attendanceService = attendanceService;
    }

    // Redirect root ke /index
    @GetMapping("/")
    public String root() {
        return "home";
    }

    // Home page (Dashboard)
    @GetMapping("/index")
    public String home(Model model, Principal principal,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // model.addAttribute("totalGrade", gradeService.getAllGrade().size());
        model.addAttribute("totalUsers", userService.getAllUser().size());
        model.addAttribute("totalStudents", studentServices.getAllUserStudent().size());
        model.addAttribute("totalCompanySpv", companySupervisorService.getCompanySupervisor().size());
        model.addAttribute("totalSchoolSpv", schoolSupervisorService.getSchoolSupervisor().size());
        model.addAttribute("totalPlacements", placementService.getAllPlacement().size());
        model.addAttribute("totalJournal", dailyJournalService.getAllJournal().size());
        model.addAttribute("totalCompany", companyService.getAllCompanyData().size());

        long studentReportTotal = 0;
        long studentAttendanceTotal = 0;
        LocalDate defaultStart = LocalDate.now().withDayOfMonth(1);
        LocalDate defaultEnd = LocalDate.now();

        // Student attendance stats defaults
        long attendanceCount = 0;
        long presentCount = 0;
        double presentPercentage = 0.0;

        // Supervisor stats defaults
        long supervisedStudentCount = 0;
        List<PlacementModel> supervisedPlacements = List.of();

        if (principal != null) {
            String username = principal.getName();
            model.addAttribute("username", username);
            UserModel user = userService.getAllUser().stream()
                    .filter(u -> u.getUsername().equals(username))
                    .findFirst().orElse(null);

            if (user != null) {

                // ----------------------------
                // STUDENT branch
                // ----------------------------
                if (user.getRole() == UserModel.Role.Student && user.getStudent() != null) {
                    Long studentId = user.getStudent().getStudentId();

                    // Hitung Data Statistik Siswa
                    studentReportTotal = reportSubmissionService.countReportsByStudentId(studentId);
                    var myPlacement = placementService.getPlacementByStudentId(studentId).orElse(null);

                    if (myPlacement != null) {
                        // Jika URL parameter kosong, pakai Start Date dari Placement
                        if (startDate == null && myPlacement.getStartDate() != null) {
                            startDate = myPlacement.getStartDate().toLocalDate();
                        }

                        // Jika URL parameter kosong, pakai End Date dari Placement
                        if (endDate == null) {
                            if (myPlacement.getEndDate() != null) {
                                endDate = myPlacement.getEndDate().toLocalDate();
                            } else {
                                // Jika PKL masih berjalan (null), anggap sampai hari ini
                                endDate = defaultEnd;
                            }
                        }
                    }

                    // ========== hitung attendance stats untuk siswa ==========
                    attendanceCount = attendanceService.countAttendanceByStudentId(studentId);

                    List<com.sistem.monitoring.models.AttendanceModel> myAttendances = attendanceService.getByStudentId(studentId);
                    presentCount = myAttendances.stream()
                            .filter(a -> a.getPresenceStatus() != null && a.getPresenceStatus() == com.sistem.monitoring.models.AttendanceModel.Presence.PRESENT)
                            .count();

                    if (attendanceCount > 0) {
                        presentPercentage = (presentCount * 100.0) / (double) attendanceCount;
                    } else {
                        presentPercentage = 0.0;
                    }
                }

                // ----------------------------
                // SUPERVISOR branch
                // ----------------------------
                else if (user.getRole() == UserModel.Role.School_Supervisor && user.getSchoolSupervisor() != null) {
                    Long supervisorId = user.getSchoolSupervisor().getsSupervisorId();

                    // Ambil placements yang dibimbing supervisor ini
                    supervisedPlacements = placementService.getBySchoolSupervisorId(supervisorId);

                    // Hitung jumlah siswa distinct di placements tersebut
                    supervisedStudentCount = supervisedPlacements.stream()
                            .map(p -> p.getStudent())
                            .filter(Objects::nonNull)
                            .map(s -> s.getStudentId())
                            .filter(Objects::nonNull)
                            .distinct()
                            .count();
                }

                // other roles: keep defaults
            }
        }

        // Final Check: Jika masih null (misal Admin Login), pakai Default
        if (startDate == null) startDate = defaultStart;
        if (endDate == null) endDate = defaultEnd;

        // Kirim ke View
        model.addAttribute("attendanceTotal", presentCount);
        model.addAttribute("reportTotal", studentReportTotal);

        // Student attendance stats
        model.addAttribute("attendanceCount", attendanceCount);
        model.addAttribute("presentCount", presentCount);
        model.addAttribute("presentPercentage", Math.round(presentPercentage * 100.0) / 100.0);

        // Supervisor stats
        model.addAttribute("supervisedStudentCount", supervisedStudentCount);
        model.addAttribute("supervisedPlacements", supervisedPlacements);

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "index";
    }

    // ==========================================
    // 1. LOGIC REDIRECT PROFIL SAYA
    // ==========================================
    @GetMapping("/my-profile")
    public String redirectToMyProfile(Principal principal) {
        if (principal == null) {
            return "redirect:/Auth/login";
        }

        String username = principal.getName();
        UserModel user = findUserByUsername(username);

        if (user == null)
            return "redirect:/index";

        // Cek Role dan Redirect ke Halaman Detail yang sesuai
        if (user.getRole() == UserModel.Role.Student) {
            if (user.getStudent() != null) {
                return "redirect:/students/" + user.getStudent().getStudentId();
            }
        } else if (user.getRole() == UserModel.Role.School_Supervisor) {
            if (user.getSchoolSupervisor() != null) {
                return "redirect:/sch-spv/" + user.getSchoolSupervisor().getsSupervisorId();
            }
        } else if (user.getRole() == UserModel.Role.Company_Supervisor) {
            if (user.getCompanySupervisor() != null) {
                return "redirect:/comp-spv/" + user.getCompanySupervisor().getcSupervisorId();
            }
        }

        return "redirect:/index";
    }

    // ==========================================
    // 2. LOGIC REDIRECT PLACEMENT SAYA (FIXED)
    // ==========================================
    @GetMapping("/my-placements")
    public String redirectToDetailPlacements(Principal principal) {
        // 1. Cek Login
        if (principal == null) {
            return "redirect:/Auth/login";
        }

        String username = principal.getName();
        UserModel user = findUserByUsername(username);

        if (user == null)
            return "redirect:/index";

        if (user.getRole() == UserModel.Role.Student) {
            if (user.getStudent() != null) {
                Long currentStudentId = user.getStudent().getStudentId();
                PlacementModel myPlacement = placementService.getAllPlacement().stream()
                        .filter(p -> p.getStudent() != null &&
                                p.getStudent().getStudentId().equals(currentStudentId))
                        .findFirst()
                        .orElse(null);
                if (myPlacement != null) {
                    return "redirect:/placements/detail/" + myPlacement.getPlacementId();
                } else {
                    return "redirect:/placements";
                }
            }
        }

        return "redirect:/placements";
    }

    // Helper method biar codingan tidak berulang
    private UserModel findUserByUsername(String username) {
        return userService.getAllUser().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    // ==========================================
    // 3. LOGIC REDIRECT ABSENSI
    // ==========================================
    @GetMapping("/my-attendance")
    public String redirectToMyAttendance(Principal principal) {
        if (principal == null) {
            return "redirect:/Auth/login";
        }

        String username = principal.getName();
        UserModel user = findUserByUsername(username);

        if (user == null)
            return "redirect:/index";
        if (user.getRole() == UserModel.Role.Student) {
            return "redirect:/attendances/create";
        }

        return "redirect:/attendances";
    }

}
