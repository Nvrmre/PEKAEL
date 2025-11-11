package com.sistem.monitoring.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistem.monitoring.models.CompanyModel;
import com.sistem.monitoring.repositories.CompanyRepository;

@Service
public class CompanyService {
    
    @Autowired
    private CompanyRepository companyRepository;

    public List<CompanyModel> getAllCompanyData(){
        return companyRepository.findAll();
    }

    public Optional<CompanyModel> getCompanyById(Long id){
        return companyRepository.findById(id);
    }

    public CompanyModel createCompanyData(CompanyModel company){
        return companyRepository.save(company);
    }

    public CompanyModel updateCompanyData(Long id, CompanyModel updated){
        CompanyModel company = companyRepository.findById(id)
                                .orElseThrow(()-> new RuntimeException("Nothing"));
                            company.setCompanyAddress(updated.getCompanyAddress());
                            company.setCompanyName(updated.getCompanyName());
                            company.setCompanyPhone(updated.getCompanyPhone());
                            company.setContactPerson(updated.getContactPerson());
                            return companyRepository.save(company);
    }

    public void deleteCompanyData(Long id){
        companyRepository.deleteById(id);
    }
}
