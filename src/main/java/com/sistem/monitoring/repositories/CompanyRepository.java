package com.sistem.monitoring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistem.monitoring.models.CompanyModel;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyModel, Long>{
    
}
