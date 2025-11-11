package com.sistem.monitoring.services;

import java.util.List;
import java.util.Optional;
import com.sistem.monitoring.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistem.monitoring.models.StudentModel;
import com.sistem.monitoring.repositories.StudentRepository;

@Service
public class StudentServices {

    private final UserRepository userRepository;
    
    @Autowired
    private StudentRepository studentRepository;


    StudentServices(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public List<StudentModel> getAllUserStudent(){
        return studentRepository.findAll();
    }

    public Optional<StudentModel> getUserStudentById(Long id){
        return studentRepository.findById(id);
    }

    public StudentModel createNewUserStudent(StudentModel student){
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
    public void deleteStudent(Long id){
        StudentModel student = studentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
        userRepository.delete(student.getUser());
        studentRepository.delete(student);

    }
}
