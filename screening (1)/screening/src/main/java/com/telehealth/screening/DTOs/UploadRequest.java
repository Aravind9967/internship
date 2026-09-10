package com.telehealth.screening.DTOs;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadRequest {
	
	@NotNull(message = "patientId is required")
	private Long patientId;
	
	@NotNull(message = "nurseId is required")
	private Long nurseId;
	
	 @NotNull(message = "image file is required")
     private MultipartFile image;

}
