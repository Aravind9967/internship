package com.screenings.cervical_screenings.Controllers;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import com.screenings.cervical_screenings.Entitys.Escalation;
import com.screenings.cervical_screenings.Entitys.Screening;
import com.screenings.cervical_screenings.Services.EscalationService;
import com.screenings.cervical_screenings.Services.ScreeningService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/specialist")
public class SpecialistController {

    private final EscalationService escalationService;
    private final ScreeningService screeningService;

    public SpecialistController(EscalationService escalationService,
                                ScreeningService screeningService) {
        this.escalationService = escalationService;
        this.screeningService = screeningService;
    }

    @GetMapping("/review-queue")
    public List<Escalation> getReviewQueue(
            @AuthenticationPrincipal User currentUser
    ) {
     
        UUID specialistId = UUID.fromString(currentUser.getUsername()); 
        return escalationService.getPendingEscalationsForSpecialist(specialistId);
    }

    @GetMapping("/screening/{id}")
    public Screening getScreeningForReview(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser
    ) {
 
        return screeningService.getScreeningById(id);
    }
}