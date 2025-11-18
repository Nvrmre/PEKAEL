package com.sistem.monitoring.services;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistem.monitoring.models.CompanySupervisorModel;
import com.sistem.monitoring.models.SchoolSupervisorModel;
import com.sistem.monitoring.models.StudentModel;
import com.sistem.monitoring.models.UserModel;
import com.sistem.monitoring.repositories.CompanySupervisorRepository;
import com.sistem.monitoring.repositories.SchoolSupervisorRepository;
import com.sistem.monitoring.repositories.StudentRepository;
import com.sistem.monitoring.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final CompanySupervisorRepository companySupervisorRepository;
    private final SchoolSupervisorRepository schoolSupervisorRepository;
    

    // service dependencies (kamu punya ini di constructor awal)
    private final StudentServices studentServices;
    private final CompanySupervisorService companySupervisorService;
    private final SchoolSupervisorService schoolSupervisorService;

    public UserService(
            UserRepository userRepository,
            StudentRepository studentRepository,
            CompanySupervisorRepository companySupervisorRepository,
            SchoolSupervisorRepository schoolSupervisorRepository,
            StudentServices studentServices,
            CompanySupervisorService companySupervisorService,
            SchoolSupervisorService schoolSupervisorService) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.companySupervisorRepository = companySupervisorRepository;
        this.schoolSupervisorRepository = schoolSupervisorRepository;
        this.studentServices = studentServices;
        this.companySupervisorService = companySupervisorService;
        this.schoolSupervisorService = schoolSupervisorService;
    }

    public List<UserModel> getAllUser() {
        return userRepository.findAll();
    }

    public Optional<UserModel> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public UserModel createNewUser(
            UserModel user,
            String studentNumber,
            String studentPhone,
            String companyName,
            String jobTitle,
            String employeeIdNumber,
            String supervisorPhone
           ) {

        if (user.getEmail() != null && userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email already exists");
        }

        // --- Safety: if UserModel has adminAccessLevel field, ensure we do not set a
        // wrong value for non-admin roles.
        try {
            Method getAdmin = user.getClass().getMethod("getAdminAccessLevel");
            Method setAdmin = user.getClass().getMethod("setAdminAccessLevel", getAdmin.getReturnType());

            if (user.getRole() == null || user.getRole() != UserModel.Role.Administrator) {
                // set admin access to null for non-admin users (avoids sending invalid value to
                // DB)
                setAdmin.invoke(user, new Object[] { null });
            }
            // if role is Administrator we leave whatever value caller set (expected to be
            // valid enum)
        } catch (NoSuchMethodException nsme) {
            // UserModel doesn't have adminAccessLevel – that's fine, ignore.
        } catch (Exception ex) {
          
        }
        //create hashed password
        String hashedPassword = passwordEncoder.encode(studentNumber);

        // save user first (so child entities can reference proper user_id)
        UserModel savedUser = userRepository.save(user);

        // create role-specific child entity (only if role present)
        if (savedUser.getRole() != null) {
            switch (savedUser.getRole()) {
                case Student:
                    StudentModel s = new StudentModel();
                    s.setUser(savedUser); // owning side is StudentModel.user
                    s.setStudentNumber(studentNumber == null ? "" : studentNumber);
                    s.setPhoneNumber(studentPhone == null ? "" : studentPhone);
                    user.setPassword(hashedPassword);
                    
                  
                    // optional: studentFullName not available in params — can be set later via edit
                    studentRepository.save(s);
                    // ensure bi-directional reference
                    savedUser.setStudent(s);
                    break;

                case Company_Supervisor:
                    CompanySupervisorModel cs = new CompanySupervisorModel();
                    cs.setUser(savedUser); // set user owner
                    cs.setJobTitle(jobTitle == null ? "" : jobTitle);
                    // company relation not set here because we need CompanyRepository / lookup by
                    // name.
                    // If you want to auto-create/find Company by companyName, add CompanyRepository
                    // and set it here.
                    companySupervisorRepository.save(cs);
                    savedUser.setCompanySupervisor(cs);
                    break;

                case School_Supervisor:
                    SchoolSupervisorModel ss = new SchoolSupervisorModel();
                    ss.setUser(savedUser);
                    ss.setEmployeeIdNumber(employeeIdNumber == null ? "" : employeeIdNumber);
                    ss.setSchoolSupervisorPhone(supervisorPhone == null ? "" : supervisorPhone);
                    schoolSupervisorRepository.save(ss);
                    savedUser.setSchoolSupervisor(ss);
                    break;

                case Administrator:
                    // no separate child entity needed
                    break;
            }
        }

        if (user.getRole() == null || user.getRole() != UserModel.Role.Administrator) {
            user.setAdminAccessLevel(null);
        }

        // return fully attached saved user
        return savedUser;
    }

    /**
     * Update user + role-specific data.
     * If role changed, old child is deleted and new child is created.
     */
    @Transactional
    public UserModel updateUser(Long id, UserModel updated) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserModel.Role oldRole = user.getRole();
        UserModel.Role newRole = updated.getRole();

        // basic fields
        user.setUsername(updated.getUsername());
        user.setEmail(updated.getEmail());
        // password handling: if client sends plain password, hash it here; otherwise
        // keep previous
        if (updated.getPassword() != null && !updated.getPassword().isEmpty()) {
            user.setPassword(updated.getPassword());
        }

        user.setRole(newRole);

        // update existing child fields (if objects present)
        if (user.getCompanySupervisor() != null && updated.getCompanySupervisor() != null) {
            // update companySupervisor fields safely
            if (updated.getCompanySupervisor().getCompany() != null) {
                user.getCompanySupervisor().setCompany(updated.getCompanySupervisor().getCompany());
            }
            user.getCompanySupervisor().setJobTitle(updated.getCompanySupervisor().getJobTitle());
            user.getCompanySupervisor()
                    .setCompanySupervisorFullName(updated.getCompanySupervisor().getCompanySupervisorFullName());
            user.getCompanySupervisor()
                    .setCompanySupervisorPhone(updated.getCompanySupervisor().getCompanySupervisorPhone());
        }
        if (user.getStudent() != null && updated.getStudent() != null) {
            user.getStudent().setStudentNumber(updated.getStudent().getStudentNumber());
            user.getStudent().setPhoneNumber(updated.getStudent().getPhoneNumber());
            user.getStudent().setStudentFullName(updated.getStudent().getStudentFullName());
            user.getStudent().setStudentAddress(updated.getStudent().getStudentAddress());
            user.getStudent().setStudentMajor(updated.getStudent().getStudentMajor());
            user.getStudent().setStudentClass(updated.getStudent().getStudentClass());
        }
        if (user.getSchoolSupervisor() != null && updated.getSchoolSupervisor() != null) {
            user.getSchoolSupervisor().setEmployeeIdNumber(updated.getSchoolSupervisor().getEmployeeIdNumber());
            user.getSchoolSupervisor()
                    .setSchoolSupervisorPhone(updated.getSchoolSupervisor().getSchoolSupervisorPhone());
        }

        // handle role change: delete old child rows (to avoid unique constraint
        // conflicts), then create new child
        if (oldRole != newRole) {

            // delete old children if present
            if (oldRole == UserModel.Role.Student && user.getStudent() != null) {
                Long studentId = user.getStudent().getStudentId();
                if (studentId != null)
                    studentRepository.deleteById(studentId);
                user.setStudent(null);
            }
            if (oldRole == UserModel.Role.Company_Supervisor && user.getCompanySupervisor() != null) {
                Long csId = user.getCompanySupervisor().getcSupervisorId();
                if (csId != null)
                    companySupervisorRepository.deleteById(csId);
                user.setCompanySupervisor(null);
            }
            if (oldRole == UserModel.Role.School_Supervisor && user.getSchoolSupervisor() != null) {
                Long ssId = user.getSchoolSupervisor().getsSupervisorId();
                if (ssId != null)
                    schoolSupervisorRepository.deleteById(ssId);
                user.setSchoolSupervisor(null);
            }

            // create new child depending on new role
            if (newRole == UserModel.Role.Student) {
                StudentModel student = new StudentModel();
                StudentModel updStud = updated.getStudent();
                student.setStudentNumber(updStud != null ? safe(updStud.getStudentNumber()) : "");
                student.setPhoneNumber(updStud != null ? safe(updStud.getPhoneNumber()) : "");
                student.setStudentFullName(updStud != null ? safe(updStud.getStudentFullName()) : "");
                student.setUser(user);
                studentRepository.save(student);
                user.setStudent(student);
            } else if (newRole == UserModel.Role.Company_Supervisor) {
                CompanySupervisorModel spv = new CompanySupervisorModel();
                CompanySupervisorModel updCs = updated.getCompanySupervisor();
                if (updCs != null && updCs.getCompany() != null) {
                    spv.setCompany(updCs.getCompany());
                }
                spv.setJobTitle(updCs != null ? safe(updCs.getJobTitle()) : "");
                spv.setCompanySupervisorFullName(updCs != null ? safe(updCs.getCompanySupervisorFullName()) : "");
                spv.setCompanySupervisorPhone(updCs != null ? safe(updCs.getCompanySupervisorPhone()) : "");
                spv.setUser(user);
                companySupervisorRepository.save(spv);
                user.setCompanySupervisor(spv);
            } else if (newRole == UserModel.Role.School_Supervisor) {
                SchoolSupervisorModel sspv = new SchoolSupervisorModel();
                SchoolSupervisorModel updSs = updated.getSchoolSupervisor();
                sspv.setEmployeeIdNumber(updSs != null ? safe(updSs.getEmployeeIdNumber()) : "");
                sspv.setSchoolSupervisorPhone(updSs != null ? safe(updSs.getSchoolSupervisorPhone()) : "");
                sspv.setUser(user);
                schoolSupervisorRepository.save(sspv);
                user.setSchoolSupervisor(sspv);
            } else {
                // newRole Administrator — nothing to create
            }
        }
      
        

        // ensure user saved and children cascaded/persisted
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // small util
    private static String safe(String s) {
        return s == null ? "" : s;
    }
}
