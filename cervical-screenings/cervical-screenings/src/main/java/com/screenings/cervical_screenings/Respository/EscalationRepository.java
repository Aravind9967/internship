package com.screenings.cervical_screenings.Respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.screenings.cervical_screenings.Entitys.Escalation;

import java.util.List;
import java.util.UUID;

public interface EscalationRepository extends JpaRepository<Escalation, UUID> {
    List<Escalation> findByAssignedSpecialistIdAndStatus(UUID specialistId, Escalation.EscalationStatus status);
}