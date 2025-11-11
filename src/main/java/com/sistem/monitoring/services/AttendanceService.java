package com.sistem.monitoring.services;

import com.sistem.monitoring.models.AttendanceModel;
import com.sistem.monitoring.repositories.AttendanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public List<AttendanceModel> getAllAttendances() {
        return attendanceRepository.findAll();
    }

    
    public Optional<AttendanceModel> getAttendanceById(Long id) {
        return attendanceRepository.findById(id);
    }


    public AttendanceModel createAttendance(AttendanceModel attendance) {
        return attendanceRepository.save(attendance);
    }

    public AttendanceModel updateAttendance(Long id, AttendanceModel updated) {
        AttendanceModel existing = attendanceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Attendance not found"));

        existing.setDate(updated.getDate());
        existing.setCheckInTime(updated.getCheckInTime());
        existing.setCheckOutTime(updated.getCheckOutTime());
        existing.setCheckInPhotoUrl(updated.getCheckInPhotoUrl());
        existing.setPresenceStatus(updated.getPresenceStatus());
        existing.setPlacement(updated.getPlacement());

        return attendanceRepository.save(existing);
    }

 
    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }
}
