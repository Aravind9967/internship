package com.screenings.cervical_screenings.Services;

import org.springframework.stereotype.Service;

import com.screenings.cervical_screenings.DTOs.ClassificationResponse;

import java.util.Random;

@Service
public class AiClassificationService {

 private final Random random = new Random();

 public ClassificationResponse classify(String imageRef) {

     try { Thread.sleep(200); } catch (InterruptedException ignored) {}

     int r = random.nextInt(3);
     String score = switch (r) {
         case 0 -> "LOW";
         case 1 -> "MEDIUM";
         default -> "HIGH";
     };
     return new ClassificationResponse(score);
 }
}