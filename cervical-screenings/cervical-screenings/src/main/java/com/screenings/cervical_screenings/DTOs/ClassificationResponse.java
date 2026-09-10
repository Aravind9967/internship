package com.screenings.cervical_screenings.DTOs;

import lombok.Data;

@Data
public class ClassificationResponse {
	private String riskScore;
	
	public ClassificationResponse() {
		
	}
	  public ClassificationResponse(String riskScore) {
	        this.riskScore = riskScore;
	    }

}
