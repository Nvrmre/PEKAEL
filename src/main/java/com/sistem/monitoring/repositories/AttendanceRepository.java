package com.sistem.monitoring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistem.monitoring.models.AttendanceModel;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceModel, Long> {
    
}
