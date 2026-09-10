package com.screenings.cervical_screenings.Services;

import org.springframework.stereotype.Service;

import com.screenings.cervical_screenings.Entitys.AuditLog;
import com.screenings.cervical_screenings.Respository.AuditLogRepository;

@Service
public class AuditLogService {

 private final AuditLogRepository auditLogRepository;

 public AuditLogService(AuditLogRepository auditLogRepository) {
     this.auditLogRepository = auditLogRepository;
 }

 public void log(String actorUserId, String action, String targetType, String targetId, String details) {
     AuditLog log = new AuditLog();
     log.setActorUserId(actorUserId);
     log.setAction(action);
     log.setTargetType(targetType);
     log.setTargetId(targetId);
     log.setDetails(details);
     auditLogRepository.save(log);
 }
}