package com.screenings.cervical_screenings.Respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.screenings.cervical_screenings.Entitys.AuditLog;

import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
}