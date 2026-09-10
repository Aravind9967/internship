package com.screenings.cervical_screenings.DTOs;

import java.util.UUID;

import lombok.Data;

@Data
public class ScreeningUploadRequest {
	 private UUID patientId;
	 private UUID nurseId;

}
