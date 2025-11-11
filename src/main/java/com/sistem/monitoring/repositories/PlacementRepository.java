package com.sistem.monitoring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistem.monitoring.models.PlacementModel;

@Repository
public interface PlacementRepository extends JpaRepository<PlacementModel, Long> {
    
}
