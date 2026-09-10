package com.screenings.cervical_screenings.Respository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.screenings.cervical_screenings.Entitys.Screening;

public interface ScreeningRepository extends JpaRepository<Screening, UUID>{

}
