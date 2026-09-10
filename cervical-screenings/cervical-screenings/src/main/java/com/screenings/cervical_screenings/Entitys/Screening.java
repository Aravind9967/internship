package com.screenings.cervical_screenings.Entitys;


import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "screenings")
@Data
public class Screening {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID patientId;

    @Column(nullable = false)
    private UUID nurseId;

    @Column(nullable = false)
    private String imageRef; // encrypted storage key/path

    @Column(nullable = true)
    private String riskScore; // LOW, MEDIUM, HIGH or numeric

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ScreeningStatus status = ScreeningStatus.PENDING_REVIEW;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public enum ScreeningStatus {
        PENDING_REVIEW,
        REVIEWED,
        OVERRIDDEN,
        COMPLETED
    }
}
