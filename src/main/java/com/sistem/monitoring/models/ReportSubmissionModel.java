package com.sistem.monitoring.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "report_submissions")
public class ReportSubmissionModel {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long submissionId;
    
    @ManyToOne
    @JoinColumn(name = "createdBy")
    private UserModel createdBy;

    @ManyToOne
    @JoinColumn(name = "studentId") 
    private StudentModel student;

    @ManyToOne
    @JoinColumn(name = "placementId")
    private PlacementModel placement;

    private String fileTitle;
    private String filePath;
    private String studentNotes;
    private String supervisorNotes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status{
        PENDING,  
        REVIEW,    
        REVISION,   
        APPROVED,   
        REJECTED
    }

    public ReportSubmissionModel(){}

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }

    public PlacementModel getPlacement() {
        return placement;
    }

    public void setPlacement(PlacementModel placement) {
        this.placement = placement;
    }

    public String getFileTitle() {
        return fileTitle;
    }

    public void setFileTitle(String fileTitle) {
        this.fileTitle = fileTitle;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getStudentNotes() {
        return studentNotes;
    }

    public void setStudentNotes(String studentNotes) {
        this.studentNotes = studentNotes;
    }

    public String getSupervisorNotes() {
        return supervisorNotes;
    }

    public void setSupervisorNotes(String supervisorNotes) {
        this.supervisorNotes = supervisorNotes;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public StudentModel getStudent() {
        return student;
    }

    public void setStudent(StudentModel student) {
        this.student = student;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UserModel getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserModel createdBy) {
        this.createdBy = createdBy;
    }

    
    
}
