package com.sistem.monitoring.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistem.monitoring.models.AttendanceModel;
import com.sistem.monitoring.models.PlacementModel;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceModel, Long> {
     boolean existsByPlacementAndDateBetween(PlacementModel placement, LocalDateTime from, LocalDateTime to);
     List<AttendanceModel> findByPlacementStudentStudentId(Long StudentId);
     Long countByPlacementStudentStudentId(Long StudentId);

    List<AttendanceModel> findByStudent_StudentId(Long studentId);
    List<AttendanceModel> findByStudent_StudentIdIn(List<Long> studentIds);
}
