package com.sistem.monitoring.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistem.monitoring.models.SchoolSupervisorModel;
import com.sistem.monitoring.models.UserModel;



@Repository
public interface SchoolSupervisorRepository extends JpaRepository<SchoolSupervisorModel, Long> {

    Optional<SchoolSupervisorModel> findByUser(UserModel user);
    
}
