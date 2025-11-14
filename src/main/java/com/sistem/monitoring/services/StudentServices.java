package com.sistem.monitoring.services;

import java.util.List;
import java.util.Optional;
import com.sistem.monitoring.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistem.monitoring.models.StudentModel;
import com.sistem.monitoring.models.UserModel;
import com.sistem.monitoring.repositories.PlacementRepository;
import com.sistem.monitoring.repositories.StudentRepository;

@Service
public class StudentServices {

    @Autowired
    private final UserRepository userRepository;
    private final PlacementRepository placementRepository;
    private final StudentRepository studentRepository;


    StudentServices(UserRepository userRepository, PlacementRepository placementRepository, StudentRepository studentRepository) {
        this.userRepository = userRepository;
        this.placementRepository = placementRepository;
        this.studentRepository = studentRepository;
    }


    public List<StudentModel> getAllUserStudent(){
        return studentRepository.findAll();
    }

    public Optional<StudentModel> getUserStudentById(Long id){
        return studentRepository.findById(id);
    }

    public StudentModel createNewUserStudent(StudentModel student){
        UserModel user = student.getUser();
        user.setStudent(student);
        return studentRepository.save(student);
    }

    public StudentModel updateUserStudent(Long id, StudentModel updated){
        StudentModel user = studentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
               user.setPhoneNumber(updated.getPhoneNumber());
               user.setStudentNumber(updated.getStudentNumber());
               return studentRepository.save(user);
    }

    @Transactional
    public void deleteStudent(Long studentId){
         if (placementRepository.existsByStudentStudentId(studentId)) {
        throw new RuntimeException("Tidak bisa menghapus siswa karena masih memiliki placement.");
    }

        StudentModel student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));

        // jika ingin juga menghapus user:
        UserModel user = student.getUser();
        studentRepository.delete(student);
        if (user != null) {
            userRepository.delete(user);
        }
    }
}
