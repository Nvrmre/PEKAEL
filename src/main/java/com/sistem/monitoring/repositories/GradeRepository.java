package com.sistem.monitoring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistem.monitoring.models.GradeModel;

public interface GradeRepository extends JpaRepository<GradeModel, Long> {
    
}
