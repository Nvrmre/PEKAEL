package com.sistem.monitoring.models;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_supervisors")
public class CompanySupervisorModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cSupervisorId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false, unique = true)
    private UserModel user;


    @Column(nullable = false)
    private String jobTitle;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "company_id", referencedColumnName = "companyId")
    private CompanyModel company;
    private String companySupervisorFullName;

    private String companySupervisorPhone;
    private String companySupervisorAddress;
    private String companyLocation;
    private String companyIndustry;
    public Long getcSupervisorId() {
        return cSupervisorId;
    }
    public void setcSupervisorId(Long cSupervisorId) {
        this.cSupervisorId = cSupervisorId;
    }
    public UserModel getUser() {
        return user;
    }
    public void setUser(UserModel user) {
        this.user = user;
    }
    public String getJobTitle() {
        return jobTitle;
    }
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
    public CompanyModel getCompany() {
        return company;
    }
    public void setCompany(CompanyModel company) {
        this.company = company;
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
    public String getCompanySupervisorAddress() {
        return companySupervisorAddress;
    }
    public void setCompanySupervisorAddress(String companySupervisorAddress) {
        this.companySupervisorAddress = companySupervisorAddress;
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





}
