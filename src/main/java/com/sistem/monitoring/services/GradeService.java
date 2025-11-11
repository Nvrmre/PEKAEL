package com.sistem.monitoring.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistem.monitoring.models.GradeModel;
import com.sistem.monitoring.repositories.GradeRepository;

@Service
public class GradeService {

    @Autowired
    private GradeRepository gradeRepository;

    public List<GradeModel> getAllGrade(){
        return gradeRepository.findAll();
    }

    public Optional<GradeModel> getGradeById(Long id){
        return gradeRepository.findById(id);
    }

    public GradeModel createGrade(GradeModel grade){
        return gradeRepository.save(grade);
    }

    public GradeModel updateGrade(Long id, GradeModel updated){
        GradeModel grade = gradeRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Nothing"));
                        grade.setPlacement(updated.getPlacement());
                        grade.setSchoolSupervisorScore(updated.getSchoolSupervisorScore());
                        grade.setCompanySupervisorScore(updated.getCompanySupervisorScore());
                        grade.setFinalScore(updated.getFinalScore());
                        grade.setFinalNotes(updated.getFinalNotes());
                        grade.setReportScore(updated.getReportScore());
                        return gradeRepository.save(grade);
    }

    public void deleteById(Long id){
        gradeRepository.deleteById(id);
    }
    
    
}
