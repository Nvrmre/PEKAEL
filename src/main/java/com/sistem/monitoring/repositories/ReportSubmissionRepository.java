package com.sistem.monitoring.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistem.monitoring.models.ReportSubmissionModel;

@Repository
public interface ReportSubmissionRepository extends JpaRepository<ReportSubmissionModel, Long> {
    List<ReportSubmissionModel> findByStudentStudentId(Long StudentId);
    Long countByStudentStudentId(Long StudentId);
}
