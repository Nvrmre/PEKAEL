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
@Table(name = "placements")
public class PlacementModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placementId;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "studentId")
    private StudentModel student;

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "companyId")
    private CompanyModel company;

    @ManyToOne
    @JoinColumn(name = "school_supervisor_id", referencedColumnName = "sSupervisorId")
    private SchoolSupervisorModel schoolSupervisor;

    @ManyToOne
    @JoinColumn(name = "company_supervisor_id", referencedColumnName = "cSupervisorId")
    private CompanySupervisorModel companySupervisor;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private Status status;



    public Long getPlacementId() {
        return placementId;
    }



    public void setPlacementId(Long placementId) {
        this.placementId = placementId;
    }



    public StudentModel getStudent() {
        return student;
    }



    public void setStudent(StudentModel student) {
        this.student = student;
    }



    public CompanyModel getCompany() {
        return company;
    }



    public void setCompany(CompanyModel company) {
        this.company = company;
    }



    public SchoolSupervisorModel getSchoolSupervisor() {
        return schoolSupervisor;
    }



    public void setSchoolSupervisor(SchoolSupervisorModel schoolSupervisor) {
        this.schoolSupervisor = schoolSupervisor;
    }



    public CompanySupervisorModel getCompanySupervisor() {
        return companySupervisor;
    }



    public void setCompanySupervisor(CompanySupervisorModel companySupervisor) {
        this.companySupervisor = companySupervisor;
    }



    public LocalDateTime getStartDate() {
        return startDate;
    }



    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }



    public LocalDateTime getEndDate() {
        return endDate;
    }



    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }



    public Status getStatus() {
        return status;
    }



    public void setStatus(Status status) {
        this.status = status;
    }



    public enum Status{
        ACTIVE,
        COMPLETED,
        CANCELLED,
        PENDING
    }



    
}
