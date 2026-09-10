package com.screenings.cervical_screenings.DTOs;

import java.time.Instant;
import java.util.UUID;

import com.screenings.cervical_screenings.Entitys.Screening;

import lombok.Data;

@Data
public class ScreeningResponse {
	   private UUID id;
	    private UUID patientId;
	    private UUID nurseId;
	    private String riskScore;
	    private Screening.ScreeningStatus status;
	    private Instant createdAt;
	    public static ScreeningResponse from(Screening s) {
	        ScreeningResponse r = new ScreeningResponse();
	        r.setId(s.getId());
	        r.setPatientId(s.getPatientId());
	        r.setNurseId(s.getNurseId());
	        r.setRiskScore(s.getRiskScore());
	        r.setStatus(s.getStatus());
	        r.setCreatedAt(s.getCreatedAt());
	        return r;
	    }

}
