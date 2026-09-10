package com.telehealth.screening.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.telehealth.screening.DTOs.ClassifyResponse;
import com.telehealth.screening.DTOs.ReviewQueueItem;
import com.telehealth.screening.DTOs.ReviewResultRequest;
import com.telehealth.screening.Services.ScreeningService;
import com.telehealth.screening.entity.Screening;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ScreeningController {

    private final ScreeningService screeningService;

    // -----------1. Nurse uploads image + metadata------------------
    @PostMapping("/screening/upload")
    public ResponseEntity<Screening> upload(
            @RequestParam Long patientId,
            @RequestParam Long nurseId,
            @RequestParam MultipartFile image) {
        Screening s = screeningService.upload(patientId, nurseId, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(s);
    }

    //--------- 2. Internal — call AI classifier, store score------------
    @PostMapping("/screening/{id}/classify")
    public ResponseEntity<ClassifyResponse> classify(@PathVariable Long id) {
        return ResponseEntity.ok(screeningService.classify(id));
    }

    // ---------3. Push into SAME escalation queue as Feature 1----------------
    @PostMapping("/screening/{id}/route-to-specialist")
    public ResponseEntity<String> route(@PathVariable Long id) {
        screeningService.routeToSpecialist(id);
        return ResponseEntity.ok("Routed to specialist review queue");
    }

    //---------------- 4. Specialist-facing review queue------------
    @GetMapping("/specialist/review-queue")
    public ResponseEntity<List<ReviewQueueItem>> reviewQueue() {
        return ResponseEntity.ok(screeningService.getReviewQueue());
    }

    //---------------- 5. Specialist confirms / overrides → result to patient/nurse-----------
    @PostMapping("/screening/{id}/result")
    public ResponseEntity<Screening> result(
            @PathVariable Long id,
            @Valid @RequestBody ReviewResultRequest request) {
        return ResponseEntity.ok(screeningService.submitResult(id, request));
    }

    //---------- Secure image view (specialist only in production — add role check)-----------
    @GetMapping("/screening/images/{id}")
    public ResponseEntity<byte[]> image(@PathVariable Long id) {
        byte[] img = screeningService.getImage(id);
        String contentType = screeningService.getImageContentType(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header("Content-Disposition", "inline; filename=\"screening-" + id + "\"")
                .body(img);
    }
}