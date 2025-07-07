package com.salesanalytics.controller;

import com.salesanalytics.dto.Recommendation;
import com.salesanalytics.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class RecommendationController {
    
    private final RecommendationService recommendationService;
    
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }
    
    @GetMapping
    public ResponseEntity<List<Recommendation>> getRecommendations() {
        try {
            List<Recommendation> recommendations = recommendationService.getRecommendations();
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}