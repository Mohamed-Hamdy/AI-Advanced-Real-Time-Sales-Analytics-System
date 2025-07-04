package com.analytics.salessystem.Controller;


import com.analytics.salessystem.Service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/recommendations")
    public Map<String, String> getRecommendations() {
        String aiSuggestion = recommendationService.getOpenAIRecommendation();
        String weatherSuggestion = recommendationService.getWeatherAdvice();

        Map<String, String> response = new HashMap<>();
        response.put("aiSuggestion", aiSuggestion);
        response.put("weatherSuggestion", weatherSuggestion);

        return response;
    }
}
