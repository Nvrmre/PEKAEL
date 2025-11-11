package com.sistem.monitoring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistem.monitoring.models.ReportSubmissionModel;

@Repository
public interface ReportSubmissionRepository extends JpaRepository<ReportSubmissionModel, Long> {
    
}
