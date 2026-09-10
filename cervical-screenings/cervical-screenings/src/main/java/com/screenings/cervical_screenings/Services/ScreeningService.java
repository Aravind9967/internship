package com.screenings.cervical_screenings.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.screenings.cervical_screenings.DTOs.ClassificationResponse;
import com.screenings.cervical_screenings.DTOs.ScreeningResultRequest;
import com.screenings.cervical_screenings.Entitys.Escalation;
import com.screenings.cervical_screenings.Entitys.Screening;
import com.screenings.cervical_screenings.Respository.ScreeningRepository;

import java.io.IOException;
import java.util.UUID;

@Service
public class ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final ImageStorageService imageStorageService;
    private final AiClassificationService aiClassificationService;
    private final EscalationService escalationService;
    private final AuditLogService auditLogService;

    public ScreeningService(ScreeningRepository screeningRepository,
                            ImageStorageService imageStorageService,
                            AiClassificationService aiClassificationService,
                            EscalationService escalationService,
                            AuditLogService auditLogService) {
        this.screeningRepository = screeningRepository;
        this.imageStorageService = imageStorageService;
        this.aiClassificationService = aiClassificationService;
        this.escalationService = escalationService;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public Screening createScreening(UUID patientId, UUID nurseId, MultipartFile image) throws IOException {
        String imageRef = imageStorageService.saveImage(image);

        Screening s = new Screening();
        s.setPatientId(patientId);
        s.setNurseId(nurseId);
        s.setImageRef(imageRef);
        s.setStatus(Screening.ScreeningStatus.PENDING_REVIEW);
        return screeningRepository.save(s);
    }

    @Transactional
    public Screening classifyScreening(UUID screeningId) {
        Screening s = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new RuntimeException("Screening not found"));

        ClassificationResponse resp = aiClassificationService.classify(s.getImageRef());
        s.setRiskScore(resp.getRiskScore());

        Screening saved = screeningRepository.save(s);

        auditLogService.log("SYSTEM", "AI_CLASSIFICATION", "SCREENING",
                s.getId().toString(), "AI risk score: " + resp.getRiskScore());

        return saved;
    }

    @Transactional
    public Escalation routeToSpecialist(UUID screeningId, UUID specialistId) {
        Screening s = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new RuntimeException("Screening not found"));

        Escalation e = escalationService.createScreeningEscalation(screeningId, specialistId);

        auditLogService.log("SYSTEM", "ROUTE_TO_SPECIALIST", "SCREENING",
                s.getId().toString(), "Escalation ID: " + e.getId());

        return e;
    }

    @Transactional
    public Screening submitResult(UUID screeningId, ScreeningResultRequest request, String specialistUserId) {
        Screening s = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new RuntimeException("Screening not found with ID: " + screeningId));

        String decision = (request != null && request.getSpecialistDecision() != null)
                ? request.getSpecialistDecision().toUpperCase().trim()
                : "COMPLETE";

        if ("OVERRIDE".equals(decision) || "OVERRIDDEN".equals(decision)) {
            s.setStatus(Screening.ScreeningStatus.OVERRIDDEN);
        } else if ("COMPLETE".equals(decision) || "COMPLETED".equals(decision)) {
            s.setStatus(Screening.ScreeningStatus.COMPLETED);
        } else {
            s.setStatus(Screening.ScreeningStatus.REVIEWED);
        }

        Screening saved = screeningRepository.save(s);

        auditLogService.log(
                specialistUserId != null ? specialistUserId : "SPECIALIST",
                "SUBMIT_RESULT",
                "SCREENING",
                s.getId().toString(),
                "Decision: " + decision + "; Notes: " + (request != null ? request.getNotes() : "")
        );

        return saved;
    }

    // ========== IMAGE METHODS (This makes the photo show) ==========
    public byte[] getImageBytes(UUID screeningId) throws IOException {
        Screening s = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new RuntimeException("Screening not found"));
        return imageStorageService.loadImage(s.getImageRef());
    }

    public String getImageContentType(UUID screeningId) {
        Screening s = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new RuntimeException("Screening not found"));
        return imageStorageService.getContentType(s.getImageRef());
    }

    public Screening getScreeningById(UUID screeningId) {
        return screeningRepository.findById(screeningId)
                .orElseThrow(() -> new RuntimeException("Screening not found"));
    }
}