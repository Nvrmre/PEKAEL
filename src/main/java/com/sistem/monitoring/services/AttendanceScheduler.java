package com.sistem.monitoring.services;

import com.sistem.monitoring.models.AttendanceModel;
import com.sistem.monitoring.models.PlacementModel;
import com.sistem.monitoring.repositories.AttendanceRepository;
import com.sistem.monitoring.repositories.PlacementRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;

@Service
public class AttendanceScheduler {

    private final AttendanceRepository attendanceRepository;
    private final PlacementRepository placementRepository;

    public AttendanceScheduler(AttendanceRepository attendanceRepository,
                               PlacementRepository placementRepository) {
        this.attendanceRepository = attendanceRepository;
        this.placementRepository = placementRepository;
    }

    /**
     * Run setiap hari jam 23:59 (Asia/Jakarta) untuk menandai absent bagi yang tidak melakukan check-in hari ini.
     * Sesuaikan siapa yang dianggap 'harus hadir' (semua placements aktif, atau students tertentu).
     */
    @Scheduled(cron = "0 59 23 * * *", zone = "Asia/Jakarta")
    public void markAbsentForMissingCheckin() {
        ZoneId zone = ZoneId.of("Asia/Jakarta");
        LocalDate today = LocalDate.now(zone);
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        // ambil semua placements yang relevan — sesuaikan query/fungsi di repo
        List<PlacementModel> placements = placementRepository.findAll(); // ganti dengan findAllActive() bila ada

        for (PlacementModel p : placements) {
            boolean hasToday = attendanceRepository.existsByPlacementAndDateBetween(p, startOfDay, endOfDay);
            if (!hasToday) {
                AttendanceModel a = new AttendanceModel();
                a.setPlacement(p);
                a.setDate(LocalDateTime.now(zone)); // atau startOfDay
                a.setCheckInTime(null);
                a.setCheckOutTime(null);
                a.setCheckInPhotoUrl(null);
                a.setPresenceStatus(AttendanceModel.Presence.ABSENT);
                attendanceRepository.save(a);
            }
        }
    }
}
