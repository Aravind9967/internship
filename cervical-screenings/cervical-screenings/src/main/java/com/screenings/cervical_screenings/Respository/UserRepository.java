package com.screenings.cervical_screenings.Respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.screenings.cervical_screenings.Entitys.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
}