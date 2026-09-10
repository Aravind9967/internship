package com.screenings.cervical_screenings.DTOs;

import java.util.UUID;

import lombok.Data;

@Data
public class ClassificationRequest {
	private UUID screeningId;
    private String imageRef;



}
