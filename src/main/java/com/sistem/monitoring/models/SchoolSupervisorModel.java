package com.sistem.monitoring.models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "school_supervisors")
public class SchoolSupervisorModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sSupervisorId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false, unique = true)
    private UserModel user;

    @Column(nullable = false)
    private String employeeIdNumber;

    @Column(nullable = true, length = 13)
    private String phoneNumber;

    public SchoolSupervisorModel(){}

    public Long getsSupervisorId() {
        return sSupervisorId;
    }

    public void setsSupervisorId(Long sSupervisorId) {
        this.sSupervisorId = sSupervisorId;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public String getEmployeeIdNumber() {
        return employeeIdNumber;
    }

    public void setEmployeeIdNumber(String employeeIdNumber) {
        this.employeeIdNumber = employeeIdNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
