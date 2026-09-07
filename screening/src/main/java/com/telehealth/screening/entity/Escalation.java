package com.telehealth.screening.entity;

import java.time.LocalDateTime;

import com.telehealth.screening.enums.EscalationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Escalation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "source_id", nullable = false)
	private Long sourceId;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
    private EscalationType type;
	
	private Long assignedSpecialistId;
	
	@Builder.Default
	private Boolean resolved = false;
	
	 @Column(updatable = false)
	 private LocalDateTime createdAt;

	 private LocalDateTime resolvedAt;

     @PrePersist
	 public void prePersist() {
	        this.createdAt = LocalDateTime.now();
	    }

		

}
