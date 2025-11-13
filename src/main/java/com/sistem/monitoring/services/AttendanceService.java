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

    /**
     * Create attendance for check-in.
     * Sets date/checkInTime = now (Asia/Jakarta) and presenceStatus = PRESENT.
     * If the incoming AttendanceModel contains only a placement with id, we fetch the
     * managed PlacementModel to avoid transient object errors.
     */
    public AttendanceModel createAttendance(AttendanceModel attendance) {
        ZoneId zone = ZoneId.of("Asia/Jakarta");
        LocalDateTime now = LocalDateTime.now(zone);

        // resolve placement entity to managed entity (avoid transient exception)
        if (attendance.getPlacement() != null && attendance.getPlacement().getPlacementId() != null) {
            Long placementId = attendance.getPlacement().getPlacementId();
            PlacementModel placement = placementRepository.findById(placementId)
                    .orElseThrow(() -> new RuntimeException("Placement not found: " + placementId));
            attendance.setPlacement(placement);
        }

        attendance.setDate(now); // tanggal & jam absen sama dengan waktu check-in
        attendance.setCheckInTime(now);
        attendance.setPresenceStatus(AttendanceModel.Presence.PRESENT); // otomatis hadir
        // checkOutTime, photoUrl left as-is (null) if not provided

        return attendanceRepository.save(attendance);
    }

    /**
     * Update attendance — only allow changing to SICK, PERMISION, ABSENT (tidak menerima PRESENT).
     */
    public AttendanceModel updateAttendance(Long id, AttendanceModel updated) {
        AttendanceModel existing = attendanceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Attendance not found"));

        // resolve placement if provided (same reason)
        if (updated.getPlacement() != null && updated.getPlacement().getPlacementId() != null) {
            Long placementId = updated.getPlacement().getPlacementId();
            PlacementModel placement = placementRepository.findById(placementId)
                    .orElseThrow(() -> new RuntimeException("Placement not found: " + placementId));
            existing.setPlacement(placement);
        }

        // hanya izinkan status SICK, PERMISION, ABSENT lewat edit
        AttendanceModel.Presence newStatus = updated.getPresenceStatus();
        if (newStatus != null && newStatus == AttendanceModel.Presence.PRESENT) {
            throw new IllegalArgumentException("Tidak diperbolehkan mengubah status menjadi PRESENT lewat edit");
        }
        if (newStatus != null) {
            existing.setPresenceStatus(newStatus);
        }

        // biarkan admin mengubah checkOutTime / photoUrl
        existing.setCheckOutTime(updated.getCheckOutTime());
        existing.setCheckInPhotoUrl(updated.getCheckInPhotoUrl());

        // jangan ubah checkInTime/date kecuali kamu memang ingin
        return attendanceRepository.save(existing);
    }

    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }
}
