package com.salesanalytics.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesanalytics.dto.Analytics;
import com.salesanalytics.dto.Recommendation;
import com.salesanalytics.dto.TopProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class RecommendationService {
    // Constants
    private static final String AI_FALLBACK_ID = "deepseek-fallback";
    private static final double HIGH_PERFORMANCE_THRESHOLD = 30.0;
    private static final double REVENUE_DECLINE_THRESHOLD = 0.0;
    private static final double REVENUE_SURGE_THRESHOLD = 50.0;
    private static final String[] WEATHER_CONDITIONS = {"hot", "cold", "rainy", "sunny"};

    // Dependencies
    private final OrderService orderService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final Random random;
    
    // Configuration
    private final boolean aiEnabled;
    private final String apiKey;
    private final String apiUrl;
    private final String modelName;

    @Autowired
    public RecommendationService(
            OrderService orderService,
            RestTemplate restTemplate,
            @Value("${ai.recommendations.enabled:true}") boolean aiEnabled,
            @Value("${deepseek.api.key}") String apiKey,
            @Value("${deepseek.api.url:https://api.deepseek.com/v1/chat/completions}") String apiUrl,
            @Value("${deepseek.model:deepseek-chat}") String modelName) {
        this.orderService = orderService;
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
        this.random = new Random();
        this.aiEnabled = aiEnabled;
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.modelName = modelName;
    }

    public List<Recommendation> getRecommendations() {
        Analytics analytics = orderService.getAnalytics();
        return aiEnabled ? getAIRecommendationsWithFallback(analytics) : getRuleBasedRecommendations(analytics);
    }

    private List<Recommendation> getAIRecommendationsWithFallback(Analytics analytics) {
        try {
            return getAIRecommendations(analytics);
        } catch (Exception e) {
            return getRuleBasedRecommendations(analytics);
        }
    }

    /* ===================== AI RECOMMENDATION LOGIC ===================== */
    private List<Recommendation> getAIRecommendations(Analytics analytics) {
        String prompt = createAIPrompt(analytics);
        String aiResponse = getAIResponse(prompt);
        return parseAIResponse(aiResponse);
    }

    private String createAIPrompt(Analytics analytics) {
        StringBuilder prompt = new StringBuilder()
            .append("As an e-commerce sales analytics AI, generate 3 data-driven product recommendations in JSON format ")
            .append("with these exact fields: id, title, description, category, priority, expectedOutcome. ")
            .append("Here is the sales data:\n")
            .append(String.format("- Current revenue: $%.2f%n", analytics.getTotalRevenue()))
            .append(String.format("- Revenue change: %.1f%%%n", analytics.getRevenueChange()))
            .append("- Top products:%n");

        analytics.getTopProducts().forEach(product -> 
            prompt.append(String.format("  - %s: %.1f%% of revenue%n", product.getName(), product.getPercentage())));

        prompt.append("\nRespond ONLY with valid JSON array containing recommendation objects.");
        return prompt.toString();
    }

    private String getAIResponse(String prompt) {
        validateAIServiceEnabled();
        HttpEntity<Map<String, Object>> request = createAIRequest(prompt);
        ResponseEntity<String> response = executeAIRequest(request);
        validateAIResponse(response);
        return response.getBody();
    }

    private HttpEntity<Map<String, Object>> createAIRequest(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", modelName);
        requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 1000);

        return new HttpEntity<>(requestBody, headers);
    }

    private ResponseEntity<String> executeAIRequest(HttpEntity<Map<String, Object>> request) {
        return restTemplate.postForEntity(apiUrl, request, String.class);
    }

    @SuppressWarnings("unchecked")
    private List<Recommendation> parseAIResponse(String jsonResponse) {
        try {
            Map<String, Object> responseMap = objectMapper.readValue(jsonResponse, 
                new TypeReference<Map<String, Object>>() {});
            
            String content = extractContentFromResponse(responseMap);
            return objectMapper.readValue(content, new TypeReference<List<Recommendation>>() {});
        } catch (Exception e) {
            return createFallbackRecommendation(jsonResponse);
        }
    }

    private String extractContentFromResponse(Map<String, Object> responseMap) {
        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        return (String) message.get("content");
    }

    private List<Recommendation> createFallbackRecommendation(String jsonResponse) {
        return List.of(new Recommendation(
            AI_FALLBACK_ID,
            "AI Analysis",
            jsonResponse,
            "ai",
            "high",
            "Enhanced decision making"
        ));
    }

    /* ===================== RULE-BASED RECOMMENDATION LOGIC ===================== */
    private List<Recommendation> getRuleBasedRecommendations(Analytics analytics) {
        List<Recommendation> recommendations = new ArrayList<>();
        recommendations.addAll(generateProductRecommendations(analytics.getTopProducts()));
        recommendations.addAll(generateRevenueRecommendations(analytics));
        recommendations.addAll(generateSeasonalRecommendations());
        return recommendations;
    }

    private List<Recommendation> generateProductRecommendations(List<TopProduct> topProducts) {
        List<Recommendation> recommendations = new ArrayList<>();
        
        if (topProducts.isEmpty()) {
            return recommendations;
        }

        TopProduct topProduct = topProducts.get(0);
        if (topProduct.getPercentage() > HIGH_PERFORMANCE_THRESHOLD) {
            recommendations.add(createTopProductPromotion(topProduct));
        }

        if (topProducts.size() > 1) {
            recommendations.add(createProductBundle(topProduct, topProducts.get(1)));
        }

        return recommendations;
    }

    private Recommendation createTopProductPromotion(TopProduct product) {
        return new Recommendation(
            "prod-promo-" + product.getName(),
            "Promote " + product.getName(),
            String.format("%s is performing well with %.1f%% of revenue", 
                product.getName(), product.getPercentage()),
            "promotion",
            "high",
            "Increase sales of top product"
        );
    }

    private Recommendation createProductBundle(TopProduct product1, TopProduct product2) {
        return new Recommendation(
            "bundle-" + product1.getName() + "-" + product2.getName(),
            "Bundle Opportunity",
            String.format("Create a bundle offer combining %s and %s", 
                product1.getName(), product2.getName()),
            "pricing",
            "medium",
            "Increase average order value"
        );
    }

    private List<Recommendation> generateRevenueRecommendations(Analytics analytics) {
        List<Recommendation> recommendations = new ArrayList<>();
        
        if (analytics.getRevenueChange() < REVENUE_DECLINE_THRESHOLD) {
            recommendations.add(createRevenueRecoveryRecommendation(analytics));
        } else if (analytics.getRevenueChange() > REVENUE_SURGE_THRESHOLD) {
            recommendations.add(createRevenueSurgeRecommendation(analytics));
        }
        
        return recommendations;
    }

    private Recommendation createRevenueRecoveryRecommendation(Analytics analytics) {
        return new Recommendation(
            "rev-recovery",
            "Revenue Recovery",
            String.format("Revenue decreased by %.1f%%", 
                Math.abs(analytics.getRevenueChange())),
            "strategy",
            "high",
            "Stabilize revenue"
        );
    }

    private Recommendation createRevenueSurgeRecommendation(Analytics analytics) {
        return new Recommendation(
            "rev-surge",
            "Capitalize on Growth",
            String.format("Revenue is surging with %.1f%% growth", 
                analytics.getRevenueChange()),
            "inventory",
            "medium",
            "Maintain growth momentum"
        );
    }

    private List<Recommendation> generateSeasonalRecommendations() {
        String weatherCondition = getRandomWeatherCondition();
        return List.of(createWeatherRecommendation(weatherCondition));
    }

    private String getRandomWeatherCondition() {
        return WEATHER_CONDITIONS[random.nextInt(WEATHER_CONDITIONS.length)];
    }

    private Recommendation createWeatherRecommendation(String weatherCondition) {
        switch (weatherCondition) {
            case "hot":
                return createHotWeatherRecommendation();
            case "cold":
                return createColdWeatherRecommendation();
            case "rainy":
                return createRainyWeatherRecommendation();
            default:
                return createDefaultWeatherRecommendation();
        }
    }

    private Recommendation createHotWeatherRecommendation() {
        return new Recommendation(
            "seasonal-hot",
            "Hot Weather Promotion",
            "Promote cooling products and summer accessories",
            "seasonal",
            "medium",
            "Boost seasonal product sales"
        );
    }

    private Recommendation createColdWeatherRecommendation() {
        return new Recommendation(
            "seasonal-cold",
            "Cold Weather Strategy",
            "Promote warm beverages and winter accessories",
            "seasonal",
            "medium",
            "Increase winter product sales"
        );
    }

    private Recommendation createRainyWeatherRecommendation() {
        return new Recommendation(
            "seasonal-rainy",
            "Rainy Day Specials",
            "Promote indoor entertainment and comfort products",
            "seasonal",
            "low",
            "Increase indoor product sales"
        );
    }

    private Recommendation createDefaultWeatherRecommendation() {
        return new Recommendation(
            "seasonal-default",
            "Optimize Product Mix",
            "Promote outdoor and recreational products",
            "seasonal",
            "low",
            "Increase outdoor product sales"
        );
    }

    /* ===================== VALIDATION METHODS ===================== */
    private void validateAIServiceEnabled() {
        if (!aiEnabled) {
            throw new IllegalStateException("AI service is disabled");
        }
    }

    private void validateAIResponse(ResponseEntity<String> response) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("AI API request failed: " + response.getStatusCode());
        }
    }
}