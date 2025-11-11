package com.sistem.monitoring.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistem.monitoring.models.PlacementModel;
import com.sistem.monitoring.repositories.PlacementRepository;

@Service
public class PlacementService {
    
    @Autowired
    private PlacementRepository placementRepository;

    public List<PlacementModel> getAllPlacement(){
        return placementRepository.findAll();
    }

    public Optional<PlacementModel> getPlacementById(Long id){
        return placementRepository.findById(id);
    }

    public PlacementModel createPlacement(PlacementModel placement){
        return placementRepository.save(placement);
    }

    public PlacementModel updatePlacement(Long id, PlacementModel updated){
        PlacementModel placement = placementRepository.findById(id)
                                 .orElseThrow(() -> new RuntimeException("No placement exist"));
                        placement.setCompany(updated.getCompany());
                        placement.setCompanySupervisor(updated.getCompanySupervisor());
                        placement.setSchoolSupervisor(updated.getSchoolSupervisor());
                        placement.setStartDate(updated.getStartDate());
                        placement.setEndDate(updated.getEndDate());
                        placement.setStatus(updated.getStatus());
                        placement.setStudent(updated.getStudent());
                        return placementRepository.save(placement);
    }

    public void deletePlacement(Long id){
        placementRepository.deleteById(id);
    }
    
}
