package com.sistem.monitoring.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "daily_journals")
public class DailyJournal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long journalId;

    @ManyToOne
    @JoinColumn(name = "placementId")
    private PlacementModel placement;

    private LocalDateTime date;

    private String activityDesc;

    private String supervisorNotes;
    
    private Validation validationStatus;

    public DailyJournal(){}

    public enum Validation{
        PENDING,
        APPROVE
    }

    public Long getJournalId() {
        return journalId;
    }

    public void setJournalId(Long journalId) {
        this.journalId = journalId;
    }

    public PlacementModel getPlacement() {
        return placement;
    }

    public void setPlacement(PlacementModel placement) {
        this.placement = placement;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getActivityDesc() {
        return activityDesc;
    }

    public void setActivityDesc(String activityDesc) {
        this.activityDesc = activityDesc;
    }

    public String getSupervisorNotes() {
        return supervisorNotes;
    }

    public void setSupervisorNotes(String supervisorNotes) {
        this.supervisorNotes = supervisorNotes;
    }

    public Validation getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(Validation validationStatus) {
        this.validationStatus = validationStatus;
    }

    

}
