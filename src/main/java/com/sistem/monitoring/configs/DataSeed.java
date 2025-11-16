package com.sistem.monitoring.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.sistem.monitoring.models.UserModel;
import com.sistem.monitoring.models.UserModel.Role;
import com.sistem.monitoring.repositories.UserRepository;


@Component
public class DataSeed implements CommandLineRunner{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public void run(String... args){
        if (userRepository.findByUsername("admin").isEmpty()) {
            UserModel admin = new UserModel();
            admin.setEmail("admin@admin.com");
            admin.setAdminFullName("admin santoso");
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("admin123"));
            admin.setRole(Role.Administrator);
            admin.setAdminActive(true);
            userRepository.save(admin);
            
        }
    }

    
} 