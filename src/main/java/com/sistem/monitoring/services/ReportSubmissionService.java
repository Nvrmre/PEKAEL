package com.sistem.monitoring.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistem.monitoring.models.ReportSubmissionModel;
import com.sistem.monitoring.repositories.ReportSubmissionRepository;

@Service
public class ReportSubmissionService {
    
    @Autowired
    private ReportSubmissionRepository reportSubmissionRepository;


    public List<ReportSubmissionModel> getAllSubmission(){
        return reportSubmissionRepository.findAll();
    }

    public Optional<ReportSubmissionModel> getSubmissionById(Long id){
        return reportSubmissionRepository.findById(id);
    }

    public ReportSubmissionModel createSubmission(ReportSubmissionModel report){
        return reportSubmissionRepository.save(report);
    }

    public ReportSubmissionModel updateReportSubmission(Long id, ReportSubmissionModel updated){
        ReportSubmissionModel report = reportSubmissionRepository.findById(id)
                                        .orElseThrow(()-> new RuntimeException("No report exist"));

                                report.setPlacement(updated.getPlacement());
                                report.setFileTitle(updated.getFileTitle());
                                report.setFilePath(updated.getFilePath());
                                report.setStatus(updated.getStatus());
                                report.setStudentNotes(updated.getStudentNotes());
                                report.setSupervisorNotes(updated.getSupervisorNotes());
                                return reportSubmissionRepository.save(report);
    }

    public List<ReportSubmissionModel> getReportsByStudentId(Long studentId) {
        return reportSubmissionRepository.findByStudentStudentId(studentId);
    }

    public long countReportsByStudentId(Long studentId) {
        return reportSubmissionRepository.countByStudentStudentId(studentId);
    }

    public void deleteReport(Long id){
        reportSubmissionRepository.deleteById(id);
    }
}
