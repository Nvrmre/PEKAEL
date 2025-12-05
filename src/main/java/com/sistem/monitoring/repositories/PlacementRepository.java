package com.sistem.monitoring.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sistem.monitoring.models.PlacementModel;

@Repository
public interface PlacementRepository extends JpaRepository<PlacementModel, Long> {
    boolean existsByStudentStudentId(Long studentId);
    void deleteByStudentStudentId(Long studentId);
    Optional<PlacementModel> findByStudentStudentId(Long studentId);
    List<PlacementModel> findBySchoolSupervisor_SSupervisorId(Long sSupervisorId);
    List<PlacementModel> findByCompany_CompanyIdAndStatus(Long companyId, String status);
    List<PlacementModel> findByCompany_CompanyId(Long companyId);
    List<PlacementModel> findByCompanySupervisor_CSupervisorId(Long cSupervisorId);

    @Query("SELECT p FROM PlacementModel p WHERE p.schoolSupervisor.sSupervisorId = :supervisorId AND p.company.companyId = :companyId")
    List<PlacementModel> findBySchoolSupervisorIdAndCompanyId(
        @Param("supervisorId") Long supervisorId, 
        @Param("companyId") Long companyId
    );
}

