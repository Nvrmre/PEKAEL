package com.sistem.monitoring.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistem.monitoring.models.SchoolSupervisorModel;
import com.sistem.monitoring.repositories.SchoolSupervisorRepository;

@Service
public class SchoolSupervisorService {
    
    @Autowired
    private SchoolSupervisorRepository schoolSupervisorRepository;

    public List<SchoolSupervisorModel> getSchoolSupervisor(){
        return schoolSupervisorRepository.findAll();
    }

    public Optional<SchoolSupervisorModel> getSchoolsupervisorById(Long id){
        return schoolSupervisorRepository.findById(id);
    }

    public SchoolSupervisorModel createSchoolSupervisor(SchoolSupervisorModel spv){
        return schoolSupervisorRepository.save(spv);
    }

    public SchoolSupervisorModel updateSchoolSupervisor(Long id, SchoolSupervisorModel updated){
        SchoolSupervisorModel spv = schoolSupervisorRepository.findById(id)
                                    .orElseThrow(() -> new RuntimeException("nothing"));
                            spv.setSchoolSupervisorPhone(updated.getSchoolSupervisorPhone());
                            spv.setEmployeeIdNumber(updated.getEmployeeIdNumber());
                            return schoolSupervisorRepository.save(spv);
    }

    public void deleteSchoolSupervisor(Long id){
        schoolSupervisorRepository.deleteById(id);
    }
}
