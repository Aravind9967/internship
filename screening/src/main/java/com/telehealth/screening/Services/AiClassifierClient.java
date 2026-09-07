package com.telehealth.screening.Services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AiClassifierClient {

    private final RestTemplate restTemplate;
    private final String classifierUrl;

    public AiClassifierClient(RestTemplate restTemplate,
            @Value("${screening.ai.classifier-url}") String classifierUrl) {
        this.restTemplate = restTemplate;
        this.classifierUrl = classifierUrl;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, Object> classify(byte[] imageBytes, String imageRef) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("imageRef", imageRef);
            request.put("sizeBytes", imageBytes.length);

            Map<String, Object> response =
                restTemplate.postForObject(classifierUrl + "/classify", request, Map.class);

            double score = ((Number) response.getOrDefault("riskScore", 0)).doubleValue();
            log.info("AI classified ref={} score={}", imageRef, score);
            return response;
        } catch (Exception e) {
           
            log.warn("AI service unavailable, using fallback score=0.0");
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("riskScore", 0.0);
            return fallback;
        }
    }
}