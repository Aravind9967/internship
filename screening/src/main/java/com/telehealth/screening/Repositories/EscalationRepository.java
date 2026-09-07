package com.telehealth.screening.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telehealth.screening.entity.Escalation;
import com.telehealth.screening.enums.EscalationType;

public interface EscalationRepository extends JpaRepository<Escalation, Long> {
   
    List<Escalation> findByTypeAndResolvedFalseOrderByCreatedAtAsc(EscalationType type);
}