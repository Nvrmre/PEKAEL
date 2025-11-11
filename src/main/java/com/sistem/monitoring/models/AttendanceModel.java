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
@Table(name = "Attendances")
public class AttendanceModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attendId;

    @ManyToOne
    @JoinColumn(name = "placementId")
    private PlacementModel placement;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    private String checkInPhotoUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Presence presenceStatus;

    public enum Presence{
        SICK,
        PRESENT,
        ABSENT,
        PERMISION
    }

    public AttendanceModel(){}

    public Long getAttendId() {
        return attendId;
    }

    public void setAttendId(Long attendId) {
        this.attendId = attendId;
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

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public String getCheckInPhotoUrl() {
        return checkInPhotoUrl;
    }

    public void setCheckInPhotoUrl(String checkInPhotoUrl) {
        this.checkInPhotoUrl = checkInPhotoUrl;
    }

    public Presence getPresenceStatus() {
        return presenceStatus;
    }

    public void setPresenceStatus(Presence presenceStatus) {
        this.presenceStatus = presenceStatus;
    }


    
}
