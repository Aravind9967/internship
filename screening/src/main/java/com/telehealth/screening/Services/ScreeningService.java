package com.telehealth.screening.Services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.telehealth.screening.DTOs.ClassifyResponse;
import com.telehealth.screening.DTOs.ReviewQueueItem;
import com.telehealth.screening.DTOs.ReviewResultRequest;
import com.telehealth.screening.Exceptions.ResourceNotFoundException;
import com.telehealth.screening.Repositories.ScreeningRepository;
import com.telehealth.screening.entity.Screening;
import com.telehealth.screening.enums.RiskLevel;
import com.telehealth.screening.enums.ScreeningStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScreeningService {

	@Autowired
    private final ScreeningRepository screeningRepository;
	@Autowired
    private final ImageStorageService imageStorageService;
	@Autowired
    private final AiClassifierClient aiClassifierClient;
	@Autowired
    private final EscalationService escalationService;

    //-----------------POST /screening/upload --------------
	
	
    @Transactional
    public Screening upload(Long patientId, Long nurseId, MultipartFile image) {
        String imageRef = imageStorageService.store(image);

        Screening screening = screeningRepository.save(Screening.builder()
                .patientId(patientId)
                .nurseId(nurseId)
                .imageRef(imageRef)
                .build());

        log.info("AUDIT: screening {} uploaded by nurse {}", screening.getId(), nurseId);
        return screening;
    }

    // --------- POST /screening/{id}/classify--------------
    
    @Transactional
    public ClassifyResponse classify(Long id) {
        Screening screening = getOrThrow(id);

        if (screening.getStatus() != ScreeningStatus.UPLOADED) {
            throw new IllegalStateException("Screening already classified");
        }

        byte[] imageBytes = imageStorageService.retrieve(screening.getImageRef());
        Map<String, Object> aiResult =
                aiClassifierClient.classify(imageBytes, screening.getImageRef());

        double score = ((Number) aiResult.get("riskScore")).doubleValue();
        screening.setRiskScore(score);
        screening.setRiskLevel(toLevel(score));
        screening.setStatus(ScreeningStatus.CLASSIFIED);
        screeningRepository.save(screening);

        return ClassifyResponse.builder()
                .screeningId(screening.getId())
                .riskScore(score)
                .riskLevel(screening.getRiskLevel().name())
                .status(screening.getStatus().name())
                .build();
    }

    // --------- POST /screening/{id}/route-to-specialist------------
   
    @Transactional
    public void routeToSpecialist(Long id) {
        Screening screening = getOrThrow(id);

        if (screening.getStatus() != ScreeningStatus.CLASSIFIED) {
            throw new IllegalStateException(
                "Must classify before routing to specialist");
        }

        escalationService.pushToQueue(id,
                com.telehealth.screening.enums.EscalationType.SCREENING);

        screening.setStatus(ScreeningStatus.WITH_SPECIALIST);
        screeningRepository.save(screening);
        log.info("Screening {} routed to specialist queue", id);
    }

    // ── GET /specialist/review-queue (screening items only) ──
    public List<ReviewQueueItem> getReviewQueue() {
        return escalationService
                .getQueue(com.telehealth.screening.enums.EscalationType.SCREENING)
                .stream()
                .map(esc -> {
                    Screening s = getOrThrow(esc.getSourceId());
                    return ReviewQueueItem.builder()
                            .escalationId(esc.getId())
                            .screeningId(s.getId())
                            .riskScore(s.getRiskScore())
                            .riskLevel(s.getRiskLevel() != null
                                    ? s.getRiskLevel().name() : "N/A")
                            .imageDownloadUrl("/screening/images/" + s.getId())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // ---------------POST /screening/{id}/result ---------------
    
    @Transactional
    public Screening submitResult(Long id, ReviewResultRequest request) {
        Screening screening = getOrThrow(id);

        if (screening.getStatus() != ScreeningStatus.WITH_SPECIALIST) {
            throw new IllegalStateException("Screening is not awaiting review");
        }

        screening.setSpecialistId(request.getSpecialistId());
        screening.setSpecialistNotes(request.getNotes());

        switch (request.getDecision()) {
            case "CONFIRMED" -> screening.setStatus(ScreeningStatus.CONFIRMED);
            case "OVERRIDDEN" -> screening.setStatus(ScreeningStatus.OVERRIDDEN);
            case "REJECTED" -> screening.setStatus(ScreeningStatus.REJECTED);
        }

        // Close its escalation ticket so it leaves the queue
        
        escalationService.getQueue(
                com.telehealth.screening.enums.EscalationType.SCREENING)
            .stream()
            .filter(esc -> esc.getSourceId().equals(id))
            .findFirst()
            .ifPresent(esc -> escalationService.markResolved(
                    esc.getId(), request.getSpecialistId()));

    
        log.info("AUDIT: result for screening {} by specialist {} -> {}",
                id, request.getSpecialistId(), request.getDecision());

        return screeningRepository.save(screening);
    }

    // Helper: view image (specialist only, in real life via role check)
    
    public byte[] getImage(Long screeningId) {
        return imageStorageService.retrieve(getOrThrow(screeningId).getImageRef());
    }

    private Screening getOrThrow(Long id) {
        return screeningRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Screening not found with id: " + id));
    }

    private RiskLevel toLevel(double score) {
        if (score >= 0.7) return RiskLevel.HIGH;
        if (score >= 0.4) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }
}