package com.sistem.monitoring.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistem.monitoring.models.CompanySupervisorModel;
import com.sistem.monitoring.models.UserModel;

@Repository
public interface CompanySupervisorRepository extends JpaRepository<CompanySupervisorModel, Long> {
    CompanySupervisorModel findByUser(UserModel user);
     Optional<CompanySupervisorModel> findByUser_Username(String username);


}
