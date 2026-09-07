package com.telehealth.screening.DTOs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClassifyResponse {
	private Long screeningId;
    private Double riskScore;
    private String riskLevel;
    private String status;

}
