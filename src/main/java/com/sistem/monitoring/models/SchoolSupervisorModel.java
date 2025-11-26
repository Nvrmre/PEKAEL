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
    private String schoolSupervisorPhone;

    private String schoolSupervisorFullName;
    private String schoolSupervisorAddress;
    private String schoolName;
    private String schoolSupervisorPosition;
    private String schoolSupervisorSubject;

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

    public String getSchoolSupervisorPhone() {
        return schoolSupervisorPhone;
    }

    public void setSchoolSupervisorPhone(String schoolSupervisorPhone) {
        this.schoolSupervisorPhone = schoolSupervisorPhone;
    }

    public String getSchoolSupervisorFullName() {
        return schoolSupervisorFullName;
    }

    public void setSchoolSupervisorFullName(String schoolSupervisorFullName) {
        this.schoolSupervisorFullName = schoolSupervisorFullName;
    }

    public String getSchoolSupervisorAddress() {
        return schoolSupervisorAddress;
    }

    public void setSchoolSupervisorAddress(String schoolSupervisorAddress) {
        this.schoolSupervisorAddress = schoolSupervisorAddress;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolSupervisorPosition() {
        return schoolSupervisorPosition;
    }

    public void setSchoolSupervisorPosition(String schoolSupervisorPosition) {
        this.schoolSupervisorPosition = schoolSupervisorPosition;
    }

    public String getSchoolSupervisorSubject() {
        return schoolSupervisorSubject;
    }

    public void setSchoolSupervisorSubject(String schoolSupervisorSubject) {
        this.schoolSupervisorSubject = schoolSupervisorSubject;
    }
    

    
}
