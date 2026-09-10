package com.screenings.cervical_screenings.Entitys;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Data
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String actorUserId;

    @Column(nullable = false)
    private String action; // e.g. "VIEW_SCREENING_IMAGE", "SUBMIT_RESULT"

    @Column(nullable = false)
    private String targetType; // "SCREENING", "ESCALATION"

    @Column(nullable = false)
    private String targetId;

    @Column(nullable = false)
    private Instant occurredAt = Instant.now();

    @Column(nullable = true)
    private String details;

}
