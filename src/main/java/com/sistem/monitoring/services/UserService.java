package com.sistem.monitoring.services;

import java.util.List;
import java.util.Optional;

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
    
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final CompanySupervisorRepository companySupervisorRepository;
    private final SchoolSupervisorRepository schoolSupervisorRepository;


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

    
    public List<UserModel> getAllUser(){
        return userRepository.findAll();
    }

   
    public Optional<UserModel> getUserById(Long id){
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

        
       
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email already exists");
        }

        
        // if (user.getRole() == UserModel.Role.Student) {
        //     StudentModel student = new StudentModel();
        //     student.setStudentNumber(studentNumber);
        //     student.setPhoneNumber(studentPhone);

        //     student.setUser(user);        // owner side
        //     user.setStudent(student);     // inverse side
        // } else if (user.getRole() == UserModel.Role.Company_Supervisor) {
        //     CompanySupervisorModel spv = new CompanySupervisorModel();
        //     spv.setCompany(null);
        //     spv.setJobTitle(jobTitle);

        //     spv.setUser(user);
        //     user.setCompanySupervisor(spv);
        // } else if (user.getRole() == UserModel.Role.School_Supervisor) {
        //     SchoolSupervisorModel sspv = new SchoolSupervisorModel();
        //     sspv.setEmployeeIdNumber(employeeIdNumber);
        //     sspv.setPhoneNumber(supervisorPhone);

        //     sspv.setUser(user);
        //     user.setSchoolSupervisor(sspv);
        // }

       
        return userRepository.save(user);
    }

    @Transactional
    public UserModel updateUser(Long id, UserModel updated) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserModel.Role oldRole = user.getRole();
        UserModel.Role newRole = updated.getRole();

        user.setUsername(updated.getUsername());
        user.setEmail(updated.getEmail());
        user.setPassword(updated.getPassword());
        user.setRole(newRole);

        // update data child jika ada dan kalau updated membawa data child
        if (user.getCompanySupervisor() != null && updated.getCompanySupervisor() != null) {
            user.getCompanySupervisor().setCompany(updated.getCompanySupervisor().getCompany());
            user.getCompanySupervisor().setJobTitle(updated.getCompanySupervisor().getJobTitle());
        }
        if (user.getStudent() != null && updated.getStudent() != null) {
            user.getStudent().setStudentNumber(updated.getStudent().getStudentNumber());
            user.getStudent().setPhoneNumber(updated.getStudent().getPhoneNumber());
        }
        if (user.getSchoolSupervisor() != null && updated.getSchoolSupervisor() != null) {
            user.getSchoolSupervisor().setEmployeeIdNumber(updated.getSchoolSupervisor().getEmployeeIdNumber());
            user.getSchoolSupervisor().setPhoneNumber(updated.getSchoolSupervisor().getPhoneNumber());
        }

        // Jika role berubah, hapus child lama dari DB terlebih dahulu untuk menghindari unique FK conflicts
        if (oldRole != newRole) {
            // delete old child if present
            if (oldRole == UserModel.Role.Student && user.getStudent() != null) {
                Long studentId = user.getStudent().getStudentId();
                if (studentId != null) studentRepository.deleteById(studentId);
                user.setStudent(null);
            }
            if (oldRole == UserModel.Role.Company_Supervisor && user.getCompanySupervisor() != null) {
                Long csId = user.getCompanySupervisor().getcSupervisorId();
                if (csId != null) companySupervisorRepository.deleteById(csId);
                user.setCompanySupervisor(null);
            }
            if (oldRole == UserModel.Role.School_Supervisor && user.getSchoolSupervisor() != null) {
                Long ssId = user.getSchoolSupervisor().getsSupervisorId();
                if (ssId != null) schoolSupervisorRepository.deleteById(ssId);
                user.setSchoolSupervisor(null);
            }

            // create new child depending on new role (with safe defaults, not-null fields non-null)
            if (newRole == UserModel.Role.Student) {
                StudentModel student = new StudentModel();
                student.setStudentNumber(updated.getStudent() != null ? updated.getStudent().getStudentNumber() : "");
                student.setPhoneNumber(updated.getStudent() != null ? updated.getStudent().getPhoneNumber() : "");
                student.setUser(user);
                user.setStudent(student);
            } else if (newRole == UserModel.Role.Company_Supervisor) {
                CompanySupervisorModel spv = new CompanySupervisorModel();
                spv.setCompany(updated.getCompanySupervisor() != null ? updated.getCompanySupervisor().getCompany() : null);
                spv.setJobTitle(updated.getCompanySupervisor() != null ? updated.getCompanySupervisor().getJobTitle() : "");
                spv.setUser(user);
                user.setCompanySupervisor(spv);
            } else if (newRole == UserModel.Role.School_Supervisor) {
                SchoolSupervisorModel sspv = new SchoolSupervisorModel();
                sspv.setEmployeeIdNumber(updated.getSchoolSupervisor() != null ? updated.getSchoolSupervisor().getEmployeeIdNumber() : "");
                sspv.setPhoneNumber(updated.getSchoolSupervisor() != null ? updated.getSchoolSupervisor().getPhoneNumber() : "");
                sspv.setUser(user);
                user.setSchoolSupervisor(sspv);
            }
        }

        return userRepository.save(user);
    }


    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
