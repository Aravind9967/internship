package com.screenings.cervical_screenings.Entitys;

import lombok.Data;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "escalations")
@Data
public class Escalation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EscalationType type;

    @Column(nullable = true)
    private UUID screeningId; // only for SCREENING type

    @Column(nullable = true)
    private String interpreterSessionId; // only for INTERPRETER type

    @Column(nullable = false)
    private UUID assignedSpecialistId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EscalationStatus status = EscalationStatus.PENDING;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public enum EscalationType {
        INTERPRETER,
        SCREENING
    }

    public enum EscalationStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED
    }
}
