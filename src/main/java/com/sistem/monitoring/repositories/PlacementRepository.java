package com.sistem.monitoring.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistem.monitoring.models.PlacementModel;

@Repository
public interface PlacementRepository extends JpaRepository<PlacementModel, Long> {
    boolean existsByStudentStudentId(Long studentId);
    void deleteByStudentStudentId(Long studentId);
    Optional<PlacementModel> findByStudentStudentId(Long studentId);
    List<PlacementModel> findBySchoolSupervisor_SSupervisorId(Long sSupervisorId);
}

