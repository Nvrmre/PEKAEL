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
    public List<ReportSubmissionModel> getByCreatorUsername(String username) {
        return reportSubmissionRepository.findByCreatedBy_Username(username);
    }

    public List<ReportSubmissionModel> getByStudentIds(List<Long> studentIds) {
        if (studentIds == null || studentIds.isEmpty()) return List.of();
        return reportSubmissionRepository.findByStudent_StudentIdIn(studentIds);
    }

    public List<ReportSubmissionModel> getByCreatorOrStudents(String username, List<Long> studentIds) {
        // gunakan repository combined kalau ada:
        return reportSubmissionRepository.findByCreatedBy_UsernameOrStudent_StudentIdIn(username, studentIds);
    }
}
