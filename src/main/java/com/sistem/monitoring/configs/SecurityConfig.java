package com.sistem.monitoring.configs;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.sistem.monitoring.repositories.UserRepository;
import com.sistem.monitoring.models.UserModel;
import com.sistem.monitoring.models.UserModel.Role;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return emailInput -> userRepository.findByEmail(emailInput)
                .map(user -> {
                    List<SimpleGrantedAuthority> authorities = mapRoleToAuthorities(user);
                    return org.springframework.security.core.userdetails.User
                            .withUsername(user.getUsername()) 
                            .password(user.getPassword())
                            .authorities(authorities)
                            .accountLocked(!Boolean.TRUE.equals(user.getActive()))
                            .disabled(!Boolean.TRUE.equals(user.getActive()))
                            .build();
                })
                .orElseThrow(() -> new UsernameNotFoundException("Email tidak ditemukan: " + emailInput));
    }

    private static List<SimpleGrantedAuthority> mapRoleToAuthorities(UserModel user) {
        Role r = user.getRole();
        if (r == null)
            return List.of();

        String authority;
        switch (r.name().toLowerCase()) {
            case "administrator": 
            case "Administrator": 
            case "admin":
                authority = "ADMINISTRATOR";
                break;
            case "student":
            case "Student":
                authority = "STUDENT"; 
                break;
            case "school_supervisor":
            case "School_supervisor":
                authority = "SCHOOL_SUPERVISOR";
                break;
            case "company_supervisor": 
            case "Company_supervisor": 
                authority = "COMPANY_SUPERVISOR";
                break;
            default:
                authority = r.name().toUpperCase();
        }
        return List.of(new SimpleGrantedAuthority(authority));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/","/home","/Auth/login", "/css/**", "/js/**").permitAll()

                // AREA ADMINISTRATOR
                .requestMatchers(
                    "/UserView/**",
                    "/SchoolSupervisorView/**", 
                    "/ReportSubmissionView/**",
                    "/PlacementView/**",
                    "/CompanySupervisorView/**",
                    "/CompanyView/**",
                    "/DailyJournalView/**",
                    "/StudentView/**"
                ).hasAnyAuthority("ADMINISTRATOR", "ROLE_ADMINISTRATOR") 
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/Auth/login")
                .loginProcessingUrl("/Auth/login")
                // .usernameParameter("email") 
                .defaultSuccessUrl("/index", true)
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/Auth/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            );

        return http.build();
    }
}