package com.sistem.monitoring.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistem.monitoring.models.CompanyModel;
import com.sistem.monitoring.models.PlacementModel;
import com.sistem.monitoring.repositories.CompanyRepository;
import com.sistem.monitoring.repositories.PlacementRepository;

@Service
public class PlacementService {

    private final PlacementRepository placementRepository;
    private final CompanyRepository companyRepository;

    public PlacementService(PlacementRepository placementRepository,
                            CompanyRepository companyRepository) {
        this.placementRepository = placementRepository;
        this.companyRepository = companyRepository;
    }

    public List<PlacementModel> getAllPlacement() {
        return placementRepository.findAll();
    }

    public Optional<PlacementModel> getPlacementById(Long id) {
        return placementRepository.findById(id);
    }

    // pastikan PlacementRepository punya: Optional<PlacementModel> findByStudent_StudentId(Long studentId);
    public Optional<PlacementModel> getPlacementByStudentId(Long studentId) {
        return placementRepository.findByStudentStudentId(studentId);
    }

    @Transactional
    public PlacementModel createPlacement(PlacementModel placement) {
        if (placement.getCompany() != null && placement.getCompany().getCompanyId() != null) {
            Long companyId = placement.getCompany().getCompanyId();
            CompanyModel managedCompany = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found: " + companyId));
            placement.setCompany(managedCompany);
        }
        return placementRepository.save(placement);
    }

    @Transactional
    public PlacementModel updatePlacement(Long id, PlacementModel updated) {
        PlacementModel placement = placementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Placement not found: " + id));

        placement.setCompany(updated.getCompany());
        placement.setCompanySupervisor(updated.getCompanySupervisor());
        placement.setSchoolSupervisor(updated.getSchoolSupervisor());
        placement.setStartDate(updated.getStartDate());
        placement.setEndDate(updated.getEndDate());
        placement.setStatus(updated.getStatus());
        placement.setStudent(updated.getStudent());

        return placementRepository.save(placement);
    }

    @Transactional
    public void deletePlacement(Long id) {
        if (!placementRepository.existsById(id)) {
            throw new RuntimeException("Placement not found: " + id);
        }
        placementRepository.deleteById(id);
    }

    public List<PlacementModel> getBySchoolSupervisorId(Long sSupervisorId) {
        return placementRepository.findBySchoolSupervisor_SSupervisorId(sSupervisorId);
    }

    public List<PlacementModel> findByCompanyId(Long companyId) {
        return placementRepository.findByCompany_CompanyId(companyId);
    }

     public List<PlacementModel> getByCompanyId(Long companyId) {
        return placementRepository.findByCompany_CompanyId(companyId);
    }

    public List<PlacementModel> getBySchoolSupervisorIdAndCompanyId(Long schoolSupervisorId, Long companyId) {
        return placementRepository.findBySchoolSupervisorIdAndCompanyId(schoolSupervisorId, companyId);
    }

}
