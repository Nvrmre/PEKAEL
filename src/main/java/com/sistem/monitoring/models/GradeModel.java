package com.sistem.monitoring.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "grades")
public class GradeModel {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gradeId;

    @ManyToOne
    @JoinColumn(name = "placementId")
    private PlacementModel placement;


    private Double schoolSupervisorScore;
    private Double companySupervisorScore;

    private Double reportScore;

    @Column(nullable = false)
    private Double finalScore;
    private String finalNotes;

    public GradeModel(){}

    public Long getGradeId() {
        return gradeId;
    }

    public void setGradeId(Long gradeId) {
        this.gradeId = gradeId;
    }

    public PlacementModel getPlacement() {
        return placement;
    }

    public void setPlacement(PlacementModel placement) {
        this.placement = placement;
    }

    public Double getSchoolSupervisorScore() {
        return schoolSupervisorScore;
    }

    public void setSchoolSupervisorScore(Double schoolSupervisorScore) {
        this.schoolSupervisorScore = schoolSupervisorScore;
    }

    public Double getCompanySupervisorScore() {
        return companySupervisorScore;
    }

    public void setCompanySupervisorScore(Double companySupervisorScore) {
        this.companySupervisorScore = companySupervisorScore;
    }

    public Double getReportScore() {
        return reportScore;
    }

    public void setReportScore(Double reportScore) {
        this.reportScore = reportScore;
    }

    public Double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Double finalScore) {
        this.finalScore = finalScore;
    }

    public String getFinalNotes() {
        return finalNotes;
    }

    public void setFinalNotes(String finalNotes) {
        this.finalNotes = finalNotes;
    }
    

}
