package com.sistem.monitoring.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistem.monitoring.models.CompanySupervisorModel;
import com.sistem.monitoring.models.SchoolSupervisorModel;
import com.sistem.monitoring.models.StudentModel;
import com.sistem.monitoring.models.UserModel;
import com.sistem.monitoring.repositories.UserRepository;

@Service
@Transactional
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
   

    // Constructor Injection
    public UserService(
            UserRepository userRepository
           ) {
        this.userRepository = userRepository;
        
    }

    public List<UserModel> getAllUser() {
        return userRepository.findAll();
    }

    public Optional<UserModel> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<UserModel> findByUsername (String Username){
        return userRepository.findByUsername(Username);
    }

    // ========================================================================
    // CREATE NEW USER (Full Parameter Spesifik)
    // ========================================================================
    public UserModel createNewUser(
            UserModel user,
            // Data Student
            String studentNumber, String studentPhone, String studentName,
            String studentAddress, String studentMajor, String studentClass,
            // Data School Supervisor
            String employeeIdNumber, String schoolPhone, String schoolSupervisorName,
            String schoolName, String schoolPosition, String schoolSubject,
            // Data Company Supervisor
            String companyName, String jobTitle, String companySupervisorName, String companySupervisorPhone,
            // Data Administrator
            String adminFullName, String adminPhone, String adminDept
    ) {

        // 1. Validasi Email
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            if (userRepository.findByEmail(user.getEmail()).isPresent()){
                throw new IllegalArgumentException("Email Already exist " + user.getEmail());
            }
          
        }

        // 2. Generate Password Default
        String rawPassword;
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            rawPassword = user.getPassword();
        } else if (studentNumber != null && !studentNumber.isBlank()) {
            rawPassword = studentNumber; // Default Password Siswa = NISN
        } else if (employeeIdNumber != null && !employeeIdNumber.isBlank()) {
            rawPassword = employeeIdNumber; // Default Password Guru = NIP
        } else {
            rawPassword = UUID.randomUUID().toString();
        }

        user.setPassword(passwordEncoder.encode(rawPassword));

        // 3. Reset Admin Access jika bukan Admin
        if (user.getRole() != UserModel.Role.Administrator) {
            user.setAdminAccessLevel(null);
        }

        // 4. Mapping Data ke Child Model
        if (user.getRole() != null) {
            switch (user.getRole()) {
                case Student:
                    StudentModel s = new StudentModel();
                    s.setUser(user);
                    s.setStudentNumber(safe(studentNumber));
                    s.setPhoneNumber(safe(studentPhone));
                    s.setStudentFullName(safe(studentName));
                    s.setStudentAddress(safe(studentAddress));
                    s.setStudentMajor(safe(studentMajor));
                    s.setStudentClass(safe(studentClass));
                    user.setStudent(s);
                    break;

                case Company_Supervisor:
                    CompanySupervisorModel cs = new CompanySupervisorModel();
                    cs.setUser(user);
                    cs.setJobTitle(safe(jobTitle));
                    cs.setCompanySupervisorFullName(safe(companySupervisorName));
                    cs.setCompanySupervisorPhone(safe(companySupervisorPhone));
                    // Logic CompanyName jika diperlukan
                    user.setCompanySupervisor(cs);
                    break;

                case School_Supervisor:
                    SchoolSupervisorModel ss = new SchoolSupervisorModel();
                    ss.setUser(user);
                    ss.setEmployeeIdNumber(safe(employeeIdNumber));
                    ss.setSchoolSupervisorPhone(safe(schoolPhone));
                    ss.setSchoolSupervisorFullName(safe(schoolSupervisorName));
                    ss.setSchoolName(safe(schoolName));
                    ss.setSchoolSupervisorPosition(safe(schoolPosition));
                    ss.setSchoolSupervisorSubject(safe(schoolSubject));
                    user.setSchoolSupervisor(ss);
                    break;

                case Administrator:
                    user.setAdminFullName(safe(adminFullName));
                    user.setAdminPhone(safe(adminPhone));
                    user.setAdminDepartment(safe(adminDept));
                    user.setAdminActive(true);
                    break;
            }
        }

        return userRepository.save(user);
    }

    // ========================================================================
    // UPDATE USER (Menerima Parameter Generic)
    // ========================================================================
    public UserModel updateUser(Long id, UserModel updated,
                                String genericFullName,
                                String genericPhone,
                                String genericAddress,
                                String genericIdentityNumber) {

        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserModel.Role oldRole = user.getRole();
        UserModel.Role newRole = updated.getRole();

        // 1. Update Data Dasar User
        user.setUsername(updated.getUsername());
        user.setEmail(updated.getEmail());

        // Hanya update password jika diisi
        if (updated.getPassword() != null && !updated.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updated.getPassword()));
        }

        user.setRole(newRole);

        // 2. Handle Perubahan Role (Hapus data child lama jika role berubah)
        if (oldRole != newRole) {
            if (oldRole == UserModel.Role.Student) {
                user.setStudent(null); // Orphan Removal akan menghapus di DB
            } else if (oldRole == UserModel.Role.Company_Supervisor) {
                user.setCompanySupervisor(null);
            } else if (oldRole == UserModel.Role.School_Supervisor) {
                user.setSchoolSupervisor(null);
            } else if (oldRole == UserModel.Role.Administrator) {
                user.setAdminFullName(null);
                user.setAdminPhone(null);
                user.setAdminDepartment(null);
            }
            userRepository.saveAndFlush(user); // Flush perubahan delete
        }

        // 3. Mapping Generic Input ke Spesifik Field berdasarkan Role BARU
        if (newRole == UserModel.Role.Student) {
            StudentModel student = user.getStudent();
            if (student == null) { student = new StudentModel(); student.setUser(user); }

            student.setStudentFullName(safe(genericFullName));
            student.setPhoneNumber(safe(genericPhone));
            student.setStudentAddress(safe(genericAddress));
            student.setStudentNumber(safe(genericIdentityNumber)); // NISN

            user.setStudent(student);

        } else if (newRole == UserModel.Role.School_Supervisor) {
            SchoolSupervisorModel ss = user.getSchoolSupervisor();
            if (ss == null) { ss = new SchoolSupervisorModel(); ss.setUser(user); }

            ss.setSchoolSupervisorFullName(safe(genericFullName));
            ss.setSchoolSupervisorPhone(safe(genericPhone));
            ss.setSchoolSupervisorAddress(safe(genericAddress));
            ss.setEmployeeIdNumber(safe(genericIdentityNumber)); // NIP

            user.setSchoolSupervisor(ss);

        } else if (newRole == UserModel.Role.Administrator) {
            user.setAdminFullName(safe(genericFullName));
            user.setAdminPhone(safe(genericPhone));
            user.setAdminOfficeLocation(safe(genericAddress));
            user.setAdminActive(true);

        } else if (newRole == UserModel.Role.Company_Supervisor) {
            CompanySupervisorModel cs = user.getCompanySupervisor();
            if (cs == null) { cs = new CompanySupervisorModel(); cs.setUser(user); }

            cs.setCompanySupervisorFullName(safe(genericFullName));
            cs.setCompanySupervisorPhone(safe(genericPhone));
            // cs.setCompanyAddress(safe(genericAddress)); // Jika ada

            user.setCompanySupervisor(cs);
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Helper untuk mencegah NullPointerException
    private static String safe(String s) {
        return s == null ? "" : s;
    }
}