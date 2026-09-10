package com.screenings.cervical_screenings.Controllers;

import com.screenings.cervical_screenings.DTOs.ScreeningResultRequest;
import com.screenings.cervical_screenings.Entitys.Escalation;
import com.screenings.cervical_screenings.Entitys.Screening;
import com.screenings.cervical_screenings.Services.ScreeningService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ScreeningController {

    private final ScreeningService screeningService;

    public ScreeningController(ScreeningService screeningService) {
        this.screeningService = screeningService;
    }

    // 1. Nurse uploads image
    @PostMapping("/screening/upload")
    public ResponseEntity<Screening> upload(
            @RequestParam UUID patientId,
            @RequestParam UUID nurseId,
            @RequestParam MultipartFile image) throws IOException {

        Screening s = screeningService.createScreening(patientId, nurseId, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(s);
    }

    // 2. Classify
    @PostMapping("/screening/{id}/classify")
    public ResponseEntity<Screening> classify(@PathVariable UUID id) {
        return ResponseEntity.ok(screeningService.classifyScreening(id));
    }

    // 3. Route to specialist
    @PostMapping("/screening/{id}/route-to-specialist")
    public ResponseEntity<Escalation> route(
            @PathVariable UUID id,
            @RequestParam UUID specialistId) {
        return ResponseEntity.ok(screeningService.routeToSpecialist(id, specialistId));
    }

    // 4. Specialist confirms / overrides
    @PostMapping("/screening/{id}/result")
    public ResponseEntity<Screening> submitResult(
            @PathVariable UUID id,
            @RequestBody ScreeningResultRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String specialistUserId) {

        return ResponseEntity.ok(screeningService.submitResult(id, request, specialistUserId));
    }

    // 5. View stored image  ← This is the important one
    @GetMapping("/screening/images/{id}")
    public ResponseEntity<byte[]> viewImage(@PathVariable UUID id) throws IOException {
        byte[] imageBytes = screeningService.getImageBytes(id);
        String contentType = screeningService.getImageContentType(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header("Content-Disposition", "inline; filename=\"screening-" + id + "\"")
                .body(imageBytes);
    }
}