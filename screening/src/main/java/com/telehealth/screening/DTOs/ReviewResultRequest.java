package com.telehealth.screening.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReviewResultRequest {
	@NotNull
	private Long specialistId;
	
	@NotBlank(message = "decision required: CONFIRMED / OVERRIDDEN / REJECTED")
    @Pattern(regexp = "CONFIRMED|OVERRIDDEN|REJECTED",
     message = "decision must be CONFIRMED, OVERRIDDEN or REJECTED")
	private String decision;
	
	@Size(max = 2000, message = "notes too long")
	private String notes;

}
