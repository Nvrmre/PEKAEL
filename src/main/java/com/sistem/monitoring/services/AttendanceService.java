package com.sistem.monitoring.services;

import com.sistem.monitoring.models.AttendanceModel;
import com.sistem.monitoring.models.PlacementModel;
import com.sistem.monitoring.repositories.AttendanceRepository;
import com.sistem.monitoring.repositories.PlacementRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final PlacementRepository placementRepository;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             PlacementRepository placementRepository) {
        this.attendanceRepository = attendanceRepository;
        this.placementRepository = placementRepository;
    }

    public List<AttendanceModel> getAllAttendances() {
        return attendanceRepository.findAll();
    }

    public Optional<AttendanceModel> getAttendanceById(Long id) {
        return attendanceRepository.findById(id);
    }
    public AttendanceModel createAttendance(AttendanceModel attendance) {
        ZoneId zone = ZoneId.of("Asia/Jakarta");
        LocalDateTime now = LocalDateTime.now(zone);

        if (attendance.getPlacement() != null && attendance.getPlacement().getPlacementId() != null) {
            Long placementId = attendance.getPlacement().getPlacementId();
            PlacementModel placement = placementRepository.findById(placementId)
                    .orElseThrow(() -> new RuntimeException("Placement not found: " + placementId));
            attendance.setPlacement(placement);
        }

        attendance.setDate(now);
        attendance.setCheckInTime(now);
        attendance.setPresenceStatus(AttendanceModel.Presence.PRESENT);

        return attendanceRepository.save(attendance);
    }

    public AttendanceModel updateAttendance(Long id, AttendanceModel updated) {
        AttendanceModel existing = attendanceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Attendance not found"));

        if (updated.getPlacement() != null && updated.getPlacement().getPlacementId() != null) {
            Long placementId = updated.getPlacement().getPlacementId();
            PlacementModel placement = placementRepository.findById(placementId)
                    .orElseThrow(() -> new RuntimeException("Placement not found: " + placementId));
            existing.setPlacement(placement);
        }

        AttendanceModel.Presence newStatus = updated.getPresenceStatus();
        if (newStatus != null && newStatus == AttendanceModel.Presence.PRESENT) {
            throw new IllegalArgumentException("Tidak diperbolehkan mengubah status menjadi PRESENT lewat edit");
        }
        if (newStatus != null) {
            existing.setPresenceStatus(newStatus);
        }

        existing.setCheckOutTime(updated.getCheckOutTime());
        existing.setCheckInPhotoUrl(updated.getCheckInPhotoUrl());

        return attendanceRepository.save(existing);
    }

    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }

    public List<AttendanceModel> findAttendanceByStudentId(Long StudentId){
        return attendanceRepository.findByPlacementStudentStudentId(StudentId);
    }
    public Long countAttendanceByStudentId(Long StudentId){
        return attendanceRepository.countByPlacementStudentStudentId(StudentId);
    }

    public List<AttendanceModel> getByStudentId(Long studentId) {
        return attendanceRepository.findByStudent_StudentId(studentId);
    }

    public List<AttendanceModel> getByStudentIds(List<Long> studentIds) {
        if (studentIds == null || studentIds.isEmpty()) return List.of();
        return attendanceRepository.findByStudent_StudentIdIn(studentIds);
    }
}