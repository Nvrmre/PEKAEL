package com.sistem.monitoring.configs;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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

    // 1) PasswordEncoder bean (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2) UserDetailsService yang mengambil user dari DB
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByUsername(username)
                .map(user -> {
                    List<SimpleGrantedAuthority> authorities = mapRoleToAuthorities(user);

                    return org.springframework.security.core.userdetails.User
                            .withUsername(user.getUsername())
                            .password(user.getPassword())
                            .authorities(authorities)
                            .accountLocked(!Boolean.TRUE.equals(user.getAdminActive()))
                            .disabled(!Boolean.TRUE.equals(user.getAdminActive()))
                            .build();
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private static List<SimpleGrantedAuthority> mapRoleToAuthorities(UserModel user) {
        Role r = user.getRole();
        if (r == null)
            return List.of();

        String authority;
        switch (r.name().toLowerCase()) {
            case "administrator":
            case "admin":
                authority = "ROLE_ADMIN";
                break;
            case "user":
            case "member":
                authority = "ROLE_USER";
                break;
            default:
                // fallback: normalisasi menjadi ROLE_{UPPER}
                authority = "ROLE_" + r.name().toUpperCase();
        }
        return List.of(new SimpleGrantedAuthority(authority));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/Auth/login", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated())

                .formLogin(form -> form
                        .loginPage("/Auth/login") // ← WAJIB pakai "/" di depan
                        .loginProcessingUrl("/Auth/login") // ← sama, wajib "/"
                        .defaultSuccessUrl("/index", true)
                        .permitAll())

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/Auth/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll());

        return http.build();
    }

}
