package com.sistem.monitoring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.sistem.monitoring.models.StudentModel;
import com.sistem.monitoring.models.UserModel;
import com.sistem.monitoring.services.StudentServices;
import com.sistem.monitoring.services.UserService;



@Controller
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentServices studentServices;

    @Autowired
    private UserService userService;

    // ==========================================
    // 1. LIST STUDENT (Hanya Administrator & Guru)
    // ==========================================
    @PreAuthorize("hasAnyAuthority('Administrator', 'School_Supervisor')")
    @GetMapping
    public String listOfStudent(Model model){
        model.addAttribute("students", studentServices.getAllUserStudent());
        return "StudentView/index";
    }

    // ==========================================
    // 2. DETAIL STUDENT (Admin ATAU Siswa Pemilik Akun)
    // ==========================================
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public String detailStudent(@PathVariable Long id, Model model, Authentication authentication) {
        // Cari data student berdasarkan ID (Student ID)
        StudentModel student = studentServices.getUserStudentById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + id));

        // SECURITY CHECK:
        // Izinkan jika dia Admin ATAU jika dia adalah pemilik data student ini
        if (isAdministrator(authentication) || isStudentOwner(authentication, student)) {
            model.addAttribute("student", student);
            return "StudentView/detail"; // Pastikan file html ini ada
        } else {
            throw new AccessDeniedException("Anda tidak memiliki izin untuk melihat data siswa lain.");
        }
    }

    // ==========================================
    // 3. CREATE FORM (Biasanya Admin)
    // ==========================================
    @PreAuthorize("hasAuthority('Administrator')")
    @GetMapping("/create")
    public String createStudentData(Model model){
        StudentModel student = new StudentModel();
        // Siapkan User kosong agar tidak null di form
        student.setUser(new UserModel());
        model.addAttribute("student", student);
        return "StudentView/create-form";
    }

    // ==========================================
    // 4. SAVE ACTION
    // ==========================================
    @PreAuthorize("hasAuthority('Administrator','ADMINISTRATOR')")
    @PostMapping
    public String saveStudentData(@ModelAttribute StudentModel student){
        UserModel user = student.getUser();
        if (user == null) user = new UserModel();

        // Set Role Paksa jadi Student
        user.setRole(UserModel.Role.Student);

        // Panggil UserService.createNewUser yang sudah kita update sebelumnya.
        // Kita pass parameter Student secara spesifik, sisanya null/kosong.
        userService.createNewUser(
                user,
                student.getStudentNumber(),     // NISN
                student.getPhoneNumber(),       // No HP
                student.getStudentFullName(),   // Nama Lengkap
                student.getStudentAddress(),    // Alamat
                student.getStudentMajor(),      // Jurusan
                student.getStudentClass(),      // Kelas
                // Parameter Guru (Kosongkan)
                null, null, null, null, null, null,
                // Parameter Perusahaan (Kosongkan)
                null, null, null, null,
                // Parameter Admin (Kosongkan)
                null, null, null
        );

        return "redirect:/students";
    }

    // ==========================================
    // 5. EDIT FORM (Admin ATAU Siswa Pemilik Akun)
    // ==========================================
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/edit/{id}")
    public String editStudentData(@PathVariable Long id, Model model, Authentication authentication){
        StudentModel student = studentServices.getUserStudentById(id)
                .orElseThrow(()-> new RuntimeException("Not found the user with id " + id));

        // SECURITY CHECK
        if (isAdministrator(authentication) || isStudentOwner(authentication, student)) {
            model.addAttribute("student", student);
            return "StudentView/edit-form";
        } else {
            throw new AccessDeniedException("Anda tidak berhak mengedit data siswa lain.");
        }
    }

    // ==========================================
    // 6. UPDATE ACTION
    // ==========================================
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public String updateStudent(@PathVariable Long id,
                                @ModelAttribute StudentModel studentInput,
                                Authentication authentication) {

        // Ambil data asli dari DB untuk validasi owner
        StudentModel existingStudent = studentServices.getUserStudentById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Validasi Security sebelum update
        if (!isAdministrator(authentication) && !isStudentOwner(authentication, existingStudent)) {
            throw new AccessDeniedException("Tidak ada izin update.");
        }

        // Panggil UserService update (Menggunakan method generic yang kita buat)
        // Kita ambil UserId dari student yang ada
        Long userId = existingStudent.getUser().getUserId();

        // Siapkan Model User wrapper untuk update (misal email/password berubah)
        UserModel userUpdateWrapper = new UserModel();
        userUpdateWrapper.setRole(UserModel.Role.Student);
        userUpdateWrapper.setUsername(studentInput.getUser().getUsername());
        userUpdateWrapper.setEmail(studentInput.getUser().getEmail());
        // Password logic handled in service (if empty, ignore)
        userUpdateWrapper.setPassword(studentInput.getUser().getPassword());

        // Panggil Update Service
        userService.updateUser(
                userId,
                userUpdateWrapper,
                studentInput.getStudentFullName(), // Generic Name
                studentInput.getPhoneNumber(),     // Generic Phone
                studentInput.getStudentAddress(),  // Generic Address
                studentInput.getStudentNumber()
        );

        if (isAdministrator(authentication)) {
            return "redirect:/students";
        } else {
            return "redirect:/students/" + id;
        }
    }

    // ==========================================
    // 7. DELETE (Hanya Administrator)
    // ==========================================
    // ==========================================
    // 7. DELETE (Hanya Administrator)
    // ==========================================
    @PreAuthorize("hasAuthority('Administrator','ADMINISTRATOR')")
    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Long id){
        // PENTING: Gunakan studentServices.deleteStudent(id)
        // karena di dalamnya ada pengecekan "Placement" (PKL).
        // Kalau pakai userService.deleteUser(), pengecekan itu akan terlewat.

        studentServices.deleteStudent(id);

        return "redirect:/students";
    }

    // ==========================================
    // HELPER METHODS (Security)
    // ==========================================

    private boolean isAdministrator(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(a -> a.getAuthority().toUpperCase())
                .anyMatch(role -> role.equals("ADMINISTRATOR") || role.equals("ROLE_ADMINISTRATOR"));
    }

    private boolean isStudentOwner(Authentication authentication, StudentModel student) {
        if (student == null || student.getUser() == null) return false;
        // Cek apakah username yang login == username pemilik data student ini
        return authentication.getName().equals(student.getUser().getUsername());
    }
}