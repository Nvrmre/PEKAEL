package com.sistem.monitoring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistem.monitoring.models.StudentModel;
import com.sistem.monitoring.models.UserModel;

@Repository
public interface StudentRepository extends JpaRepository<StudentModel, Long>{
    StudentModel findByUser(UserModel user);

}
