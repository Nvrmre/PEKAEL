package com.sistem.monitoring.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "users")
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;


    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "user", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
    private CompanySupervisorModel companySupervisor;

    @OneToOne(mappedBy = "user", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
    private StudentModel student;

    @OneToOne(mappedBy = "user", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
    private SchoolSupervisorModel schoolSupervisor;

    private String adminFullName;
    private String adminPhone;
    private String adminDepartment;
    private String adminOfficeLocation;

    @Enumerated(EnumType.ORDINAL)
    private AccessLevel adminAccessLevel;

    @Column(columnDefinition = "text")
    private String adminNotes;


    @Column
    private Boolean adminActive;

    public UserModel() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public CompanySupervisorModel getCompanySupervisor() {
        return companySupervisor;
    }

    public void setCompanySupervisor(CompanySupervisorModel companySupervisor) {
        this.companySupervisor = companySupervisor;
    }

    public StudentModel getStudent() {
        return student;
    }

    public void setStudent(StudentModel student) {
        this.student = student;
    }

    public SchoolSupervisorModel getSchoolSupervisor() {
        return schoolSupervisor;
    }

    public void setSchoolSupervisor(SchoolSupervisorModel schoolSupervisor) {
        this.schoolSupervisor = schoolSupervisor;
    }

    public String getAdminFullName() {
        return adminFullName;
    }

    public void setAdminFullName(String adminFullName) {
        this.adminFullName = adminFullName;
    }

    public String getAdminPhone() {
        return adminPhone;
    }

    public void setAdminPhone(String adminPhone) {
        this.adminPhone = adminPhone;
    }

    public String getAdminDepartment() {
        return adminDepartment;
    }

    public void setAdminDepartment(String adminDepartment) {
        this.adminDepartment = adminDepartment;
    }

    public String getAdminOfficeLocation() {
        return adminOfficeLocation;
    }

    public void setAdminOfficeLocation(String adminOfficeLocation) {
        this.adminOfficeLocation = adminOfficeLocation;
    }

    public AccessLevel getAdminAccessLevel() {
        return adminAccessLevel;
    }

    public void setAdminAccessLevel(AccessLevel adminAccessLevel) {
        this.adminAccessLevel = adminAccessLevel;
    }

    

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    public Boolean getAdminActive() {
        return adminActive;
    }

    public void setAdminActive(Boolean adminActive) {
        this.adminActive = adminActive;
    }



    public enum AccessLevel {
        ADMIN,
        SUPER_ADMIN
    }

    public enum Role {
        School_Supervisor,
        Company_Supervisor,
        Administrator,
        Student

    }
    public enum Permission{
        USERS_MANAGE,
        PLACEMENTS_MANAGE,
        REPORTS_VIEW,
        SETTINGS_MANAGE
    }
}
