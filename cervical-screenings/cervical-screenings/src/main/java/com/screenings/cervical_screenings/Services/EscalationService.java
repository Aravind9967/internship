package com.screenings.cervical_screenings.Services;

import org.springframework.stereotype.Service;

import com.screenings.cervical_screenings.Entitys.Escalation;
import com.screenings.cervical_screenings.Respository.EscalationRepository;

import java.util.List;
import java.util.UUID;

@Service
public class EscalationService {

 private final EscalationRepository escalationRepository;

 public EscalationService(EscalationRepository escalationRepository) {
     this.escalationRepository = escalationRepository;
 }

 public Escalation createScreeningEscalation(UUID screeningId, UUID specialistId) {
     Escalation e = new Escalation();
     e.setType(Escalation.EscalationType.SCREENING);
     e.setScreeningId(screeningId);
     e.setAssignedSpecialistId(specialistId);
     e.setStatus(Escalation.EscalationStatus.PENDING);
     return escalationRepository.save(e);
 }

 public List<Escalation> getPendingEscalationsForSpecialist(UUID specialistId) {
     return escalationRepository.findByAssignedSpecialistIdAndStatus(
             specialistId, Escalation.EscalationStatus.PENDING);
 }

 public void markCompleted(UUID escalationId) {
     Escalation e = escalationRepository.findById(escalationId)
             .orElseThrow();
     e.setStatus(Escalation.EscalationStatus.COMPLETED);
     escalationRepository.save(e);
 }
}
