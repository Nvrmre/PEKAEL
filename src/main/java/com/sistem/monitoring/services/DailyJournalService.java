package com.sistem.monitoring.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistem.monitoring.models.DailyJournal;
import com.sistem.monitoring.repositories.DailyJournalRepository;

@Service
public class DailyJournalService {
    

    @Autowired
    private DailyJournalRepository dailyJournalRepository;

    public List<DailyJournal> getAllJournal(){
        return dailyJournalRepository.findAll();
    }

    public Optional<DailyJournal> getJournalById(Long id){
        return dailyJournalRepository.findById(id);
    }

    public DailyJournal createJournal(DailyJournal journal){
        return dailyJournalRepository.save(journal);
    }

    public DailyJournal updateJournal(Long id, DailyJournal updated){
        DailyJournal journal = dailyJournalRepository.findById(id)
                                .orElseThrow(()-> new RuntimeException("No Journal Exist"));
                        journal.setPlacement(updated.getPlacement());
                        journal.setDate(updated.getDate());
                        journal.setValidationStatus(updated.getValidationStatus());
                        journal.setActivityDesc(updated.getActivityDesc());
                        journal.setSupervisorNotes(updated.getSupervisorNotes());
                        return dailyJournalRepository.save(journal);
    }

    public void deleteJournal(Long id){
        dailyJournalRepository.deleteById(id);
    }




}
