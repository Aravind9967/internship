package com.telehealth.screening.entity;

import java.time.LocalDateTime;
import com.telehealth.screening.enums.RiskLevel;
import com.telehealth.screening.enums.ScreeningStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "screenings", uniqueConstraints = {
    @UniqueConstraint(name = "uk_image_hash", columnNames = "image_hash")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Screening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private Long nurseId;

    @Column(name = "image_ref", nullable = false)
    private String imageRef;

    @Column(name = "image_hash", nullable = false, unique = true, length = 64)
    private String imageHash;

    private Double riskScore;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScreeningStatus status;

    private String specialistNotes;

    private Long specialistId;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = ScreeningStatus.UPLOADED;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}