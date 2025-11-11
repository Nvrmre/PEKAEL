package com.sistem.monitoring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistem.monitoring.models.StudentModel;

@Repository
public interface StudentRepository extends JpaRepository<StudentModel, Long>{
    
}
