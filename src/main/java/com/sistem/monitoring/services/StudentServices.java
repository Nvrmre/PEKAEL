package com.sistem.monitoring.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistem.monitoring.models.StudentModel;
import com.sistem.monitoring.models.UserModel;
import com.sistem.monitoring.repositories.PlacementRepository;
import com.sistem.monitoring.repositories.StudentRepository;
import com.sistem.monitoring.repositories.UserRepository;

@Service
public class StudentServices {

    private final UserRepository userRepository;
    private final PlacementRepository placementRepository;
    private final StudentRepository studentRepository;

    // Constructor Injection (Lebih disarankan daripada @Autowired di field)
    @Autowired
    public StudentServices(UserRepository userRepository,
                           PlacementRepository placementRepository,
                           StudentRepository studentRepository) {
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

    // Method ini tetap ada untuk backup, meskipun Controller utama sekarang menggunakan UserService
    @Transactional
    public StudentModel createNewUserStudent(StudentModel student){
        UserModel user = student.getUser();
        if (user != null) {
            user.setStudent(student); // Pastikan relasi bidirectional
        }
        return studentRepository.save(student);
    }

    // Method update sederhana (Hanya update data spesifik siswa)
    @Transactional
    public StudentModel updateUserStudent(Long id, StudentModel updated){
        StudentModel existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + id));

        existingStudent.setPhoneNumber(updated.getPhoneNumber());
        existingStudent.setStudentNumber(updated.getStudentNumber());
        // Field lain seperti Nama/Alamat biasanya diupdate lewat UserService

        return studentRepository.save(existingStudent);
    }

    @Transactional
    public void deleteStudent(Long studentId){
        // 1. Cek Constraint: Jangan hapus jika siswa sudah punya Placement (PKL)
        // Pastikan method existsByStudent_StudentId ada di PlacementRepository
        if (placementRepository.existsByStudentStudentId(studentId)) {
            throw new RuntimeException("Gagal Menghapus: Siswa ini masih memiliki data Placement/PKL aktif.");
        }

        StudentModel student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        UserModel user = student.getUser();

        // 2. Hapus Data Student dulu
        // (Terkadang perlu set user.setStudent(null) dulu jika ada constraint strict)
        studentRepository.delete(student);

        // 3. Hapus Data User (Akun Login)
        if (user != null) {
            userRepository.delete(user);
        }
    }
}