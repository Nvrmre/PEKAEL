package com.sistem.monitoring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistem.monitoring.models.CompanySupervisorModel;

@Repository
public interface CompanySupervisorRepository extends JpaRepository<CompanySupervisorModel, Long> {
    
}
