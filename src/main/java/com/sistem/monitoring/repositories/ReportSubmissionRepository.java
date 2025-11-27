package com.sistem.monitoring.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistem.monitoring.models.ReportSubmissionModel;

@Repository
public interface ReportSubmissionRepository extends JpaRepository<ReportSubmissionModel, Long> {
    List<ReportSubmissionModel> findByStudentStudentId(Long StudentId);
    Long countByStudentStudentId(Long StudentId);
    
    List<ReportSubmissionModel> findByCreatedBy_Username(String username);

    // semua report yang terkait dengan daftar student id
    List<ReportSubmissionModel> findByStudent_StudentIdIn(List<Long> studentIds);

    // gabungan: dibuat oleh username OR student id berada dalam list
    List<ReportSubmissionModel> findByCreatedBy_UsernameOrStudent_StudentIdIn(String username, List<Long> studentIds);
}
