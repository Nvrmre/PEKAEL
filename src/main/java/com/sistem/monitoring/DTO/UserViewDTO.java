package com.sistem.monitoring.DTO;

public class UserViewDTO {
    private Long userId;
    private String username;   // untuk menampilkan nama (fullName)
    private String email;
    private String role;

    // Student fields
    private String studentNumber;
    private String studentFullName;
    private String studentPhone;
    private String studentAddress;
    private String studentMajor;
    private String studentClass;

    // Company supervisor fields
    private String companySupervisorFullName;
    private String companySupervisorPhone;
    private String companyName;
    private String companyJobTitle;
    private String companyLocation;
    private String companyIndustry;

    // School supervisor fields
    private String schoolSupervisorFullName;
    private String schoolSupervisorPhone;
    private String schoolName;
    private String schoolEmployeeId;
    private String schoolPosition;
    private String schoolSubject;

    // Admin fields
    private String adminFullName;
    private String adminPhone;
    private String adminDepartment;
    private String adminOfficeLocation;
    private String adminAccessLevel;
    private String adminNotes;
    private Boolean adminActive;

    // constructors, getters, setters (only key ones shown; you can generate others)
    public UserViewDTO() {}

    // basic constructor convenience
    public UserViewDTO(Long userId, String username, String email, String role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    // getters & setters (generate with IDE) - essential ones shown:
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public String getStudentFullName() { return studentFullName; }
    public void setStudentFullName(String studentFullName) { this.studentFullName = studentFullName; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getAdminFullName() { return adminFullName; }
    public void setAdminFullName(String adminFullName) { this.adminFullName = adminFullName; }

    public Boolean getAdminActive() { return adminActive; }
    public void setAdminActive(Boolean adminActive) { this.adminActive = adminActive; }

    public String getStudentPhone() {
        return studentPhone;
    }

    public void setStudentPhone(String studentPhone) {
        this.studentPhone = studentPhone;
    }

    public String getStudentAddress() {
        return studentAddress;
    }

    public void setStudentAddress(String studentAddress) {
        this.studentAddress = studentAddress;
    }

    public String getStudentMajor() {
        return studentMajor;
    }

    public void setStudentMajor(String studentMajor) {
        this.studentMajor = studentMajor;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public String getCompanySupervisorFullName() {
        return companySupervisorFullName;
    }

    public void setCompanySupervisorFullName(String companySupervisorFullName) {
        this.companySupervisorFullName = companySupervisorFullName;
    }

    public String getCompanySupervisorPhone() {
        return companySupervisorPhone;
    }

    public void setCompanySupervisorPhone(String companySupervisorPhone) {
        this.companySupervisorPhone = companySupervisorPhone;
    }

    public String getCompanyJobTitle() {
        return companyJobTitle;
    }

    public void setCompanyJobTitle(String companyJobTitle) {
        this.companyJobTitle = companyJobTitle;
    }

    public String getCompanyLocation() {
        return companyLocation;
    }

    public void setCompanyLocation(String companyLocation) {
        this.companyLocation = companyLocation;
    }

    public String getCompanyIndustry() {
        return companyIndustry;
    }

    public void setCompanyIndustry(String companyIndustry) {
        this.companyIndustry = companyIndustry;
    }

    public String getSchoolSupervisorFullName() {
        return schoolSupervisorFullName;
    }

    public void setSchoolSupervisorFullName(String schoolSupervisorFullName) {
        this.schoolSupervisorFullName = schoolSupervisorFullName;
    }

    public String getSchoolSupervisorPhone() {
        return schoolSupervisorPhone;
    }

    public void setSchoolSupervisorPhone(String schoolSupervisorPhone) {
        this.schoolSupervisorPhone = schoolSupervisorPhone;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolEmployeeId() {
        return schoolEmployeeId;
    }

    public void setSchoolEmployeeId(String schoolEmployeeId) {
        this.schoolEmployeeId = schoolEmployeeId;
    }

    public String getSchoolPosition() {
        return schoolPosition;
    }

    public void setSchoolPosition(String schoolPosition) {
        this.schoolPosition = schoolPosition;
    }

    public String getSchoolSubject() {
        return schoolSubject;
    }

    public void setSchoolSubject(String schoolSubject) {
        this.schoolSubject = schoolSubject;
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

    public String getAdminAccessLevel() {
        return adminAccessLevel;
    }

    public void setAdminAccessLevel(String adminAccessLevel) {
        this.adminAccessLevel = adminAccessLevel;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

}
