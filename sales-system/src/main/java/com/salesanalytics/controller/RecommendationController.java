package com.salesanalytics.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.salesanalytics.dto.Recommendation;
import com.salesanalytics.service.RecommendationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationController.class);
    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public ResponseEntity<?> getRecommendations() {
        try {
            List<Recommendation> recommendations = recommendationService.getRecommendations();
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            logger.error("Failed to generate recommendations", e);
            return ResponseEntity.internalServerError().body(
                Map.of(
                    "error", "Failed to generate recommendations",
                    "message", e.getMessage(),
                    "type", e.getClass().getSimpleName()
                )
            );
        }
    }
}