package com.screenings.cervical_screenings.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                // ========== PUBLIC ENDPOINTS ==========
                .requestMatchers("/users/register").permitAll()          // ← This is the important line
                .requestMatchers("/api/users/register").permitAll()      // in case you use /api prefix

                // ========== NURSE only ==========
                .requestMatchers("/api/screening/upload").hasRole("NURSE")

                // ========== NURSE + SPECIALIST ==========
                .requestMatchers("/api/screening/**").hasAnyRole("NURSE", "SPECIALIST")
                .requestMatchers("/screening/**").hasAnyRole("NURSE", "SPECIALIST")

                // ========== SPECIALIST only ==========
                .requestMatchers("/api/specialist/**").hasRole("SPECIALIST")

                // ========== ADMIN only ==========
                .requestMatchers("/users/**", "/api/users/**").hasRole("ADMIN")

                // Everything else needs login
                .anyRequest().authenticated()
            )
            .httpBasic(withDefaults());

        return http.build();
    }
}