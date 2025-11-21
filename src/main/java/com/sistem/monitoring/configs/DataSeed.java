package com.sistem.monitoring.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.sistem.monitoring.models.UserModel;
import com.sistem.monitoring.models.UserModel.Role;
import com.sistem.monitoring.repositories.UserRepository;

@Component
public class DataSeed implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;
    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.fullname}")
    private String adminFullName;

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            UserModel admin = new UserModel();
            admin.setEmail(adminEmail);
            admin.setAdminFullName(adminFullName);
            admin.setUsername(adminUsername);
            admin.setPassword(encoder.encode(adminPassword));
            admin.setRole(Role.Administrator);
            admin.setAdminActive(true);
            userRepository.save(admin);

        }
    }

}