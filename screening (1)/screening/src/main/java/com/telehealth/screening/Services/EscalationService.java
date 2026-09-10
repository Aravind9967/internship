package com.telehealth.screening.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telehealth.screening.Repositories.EscalationRepository;
import com.telehealth.screening.entity.Escalation;
import com.telehealth.screening.enums.EscalationType;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EscalationService {

	@Autowired
    private final EscalationRepository escalationRepository;
    
    @Transactional
    public Escalation pushToQueue(Long sourceId, EscalationType type) {
        return escalationRepository.save(Escalation.builder()
                .sourceId(sourceId)
                .type(type)
                .build());
    }

    public List<Escalation> getQueue(EscalationType type) {
        return escalationRepository
                .findByTypeAndResolvedFalseOrderByCreatedAtAsc(type);
    }

    @Transactional
    public void markResolved(Long escalationId, Long specialistId) {
        Escalation esc = escalationRepository.findById(escalationId)
                .orElseThrow(() -> new RuntimeException("Escalation not found"));
        esc.setResolved(true);
        esc.setAssignedSpecialistId(specialistId);
        esc.setResolvedAt(java.time.LocalDateTime.now());
    }
}