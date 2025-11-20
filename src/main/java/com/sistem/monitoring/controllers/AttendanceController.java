package com.sistem.monitoring.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.sistem.monitoring.models.AttendanceModel;
import com.sistem.monitoring.models.PlacementModel;
import com.sistem.monitoring.models.UserModel;
import com.sistem.monitoring.services.AttendanceService;
import com.sistem.monitoring.services.PlacementService;
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

    // Lokasi folder untuk menyimpan foto (Pastikan folder ini ada atau sistem akan membuatnya)
    private final String UPLOAD_DIR = "src/main/resources/static/uploads/absensi/";

    // ==========================================
    // 1. LIST ALL DATA
    // ==========================================
    @GetMapping
    public String showAllAttendance(Model model){
        model.addAttribute("attend", attendanceService.getAllAttendances());
        return "AttendanceView/index";
    }

    // ==========================================
    // 2. FORM CREATE (PRE-FILL PLACEMENT)
    // ==========================================
    @GetMapping("/create")
    public String showFormCreate(Model model, Principal principal) {
        AttendanceModel attend = new AttendanceModel();

        // Logika Otomatis untuk Siswa -> Placement
        if (principal != null) {
            String username = principal.getName();
            
            // Cari user login
            UserModel user = userService.getAllUser().stream()
                    .filter(u -> u.getUsername().equals(username))
                    .findFirst().orElse(null);

            // Jika User = Student & Punya Data Student
            if (user != null && user.getRole() == UserModel.Role.Student && user.getStudent() != null) {
                Long studentId = user.getStudent().getStudentId();

                // Cari Placement aktif
                PlacementModel myPlacement = placementService.getPlacementByStudentId(studentId)
                        .orElse(null);

                // Jika ketemu, set ke form
                if (myPlacement != null) {
                    attend.setPlacement(myPlacement);
                }
            }
        }

        model.addAttribute("attend", attend);
        return "AttendanceView/create-form";
    }

    // ==========================================
    // 3. SAVE DATA (HANDLE FOTO & STATUS)
    // ==========================================
    @PostMapping
    public String saveData(@ModelAttribute AttendanceModel attend,
                           @RequestParam("checkInPhoto") MultipartFile file) {
        
        // --- LOGIKA UPLOAD FOTO ---
        if (!file.isEmpty()) {
            try {
                // 1. Buat nama file unik (uuid_namaasli.jpg)
                String fileName = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
                
                // 2. Siapkan Path Folder
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // 3. Simpan File ke Folder
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // 4. Simpan URL/Path-nya ke Database (untuk diakses via HTML nanti)
                // Path relatif agar bisa dibuka di browser: /uploads/absensi/namafile.jpg
                attend.setCheckInPhotoUrl("/uploads/absensi/" + fileName);

            } catch (IOException e) {
                e.printStackTrace();
                // Opsi: Bisa tambahkan error message ke redirect attributes jika mau
            }
        }

        // --- SIMPAN KE DB ---
        // Service 'createAttendance' akan otomatis set:
        // - Tanggal = Hari ini
        // - Jam = Sekarang
        // - Status = PRESENT (Hadir)
        attendanceService.createAttendance(attend);
        
        return "redirect:/attendances";
    }
    
    // ==========================================
    // 4. EDIT FORM
    // ==========================================
    @GetMapping("edit/{id}")
    public String editData(@PathVariable Long id, Model model){
        AttendanceModel attend = attendanceService.getAttendanceById(id)
                .orElseThrow(()-> new RuntimeException("Attendance Not found"));
        model.addAttribute("attend", attend);
        return "AttendanceView/edit-form";
    }

    // ==========================================
    // 5. UPDATE DATA
    // ==========================================
    @PutMapping("/{id}")
    public String updateData(@PathVariable Long id, @ModelAttribute AttendanceModel attend){
        attendanceService.updateAttendance(id, attend);
        return "redirect:/attendances";
    }

    // ==========================================
    // 6. DELETE DATA
    // ==========================================
    @DeleteMapping("/{id}")
    public String deleteData(@PathVariable Long id){
        attendanceService.deleteAttendance(id);
        return "redirect:/attendances";
    }
}