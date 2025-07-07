package com.salesanalytics.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesanalytics.dto.Analytics;
import com.salesanalytics.dto.Recommendation;
import com.salesanalytics.dto.TopProduct;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class RecommendationService {

    private final OrderService orderService;
    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    @Value("${recommendation.mode:normal}")
    private String recommendationMode;

    public RecommendationService(
            OrderService orderService,
            @Value("${openai.api.key}") String apiKey) {
        this.orderService = orderService;
        this.openAiService = new OpenAiService(apiKey, Duration.ofSeconds(30));
    }

    public List<Recommendation> getRecommendations() {
        Analytics analytics = orderService.getAnalytics();

        switch (recommendationMode.toLowerCase()) {
            case "ai":
                return getAIRecommendations(analytics);
            case "hybrid":
                List<Recommendation> hybrid = new ArrayList<>(getRuleBasedRecommendations(analytics));
                hybrid.addAll(getAIRecommendations(analytics));
                return hybrid;
            case "normal":
            default:
                return getRuleBasedRecommendations(analytics);
        }
    }

    /* ========================== RULE-BASED ========================== */

    private List<Recommendation> getRuleBasedRecommendations(Analytics analytics) {
        List<Recommendation> recommendations = new ArrayList<>();
        recommendations.addAll(generateProductRecommendations(analytics.getTopProducts()));
        recommendations.addAll(generateRevenueRecommendations(analytics));
        recommendations.addAll(generateSeasonalRecommendations());
        return recommendations;
    }

    private List<Recommendation> generateProductRecommendations(List<TopProduct> topProducts) {
        List<Recommendation> recommendations = new ArrayList<>();

        if (!topProducts.isEmpty()) {
            TopProduct topProduct = topProducts.get(0);

            if (topProduct.getPercentage() > 30) {
                recommendations.add(new Recommendation(
                        "1",
                        "Promote " + topProduct.getName(),
                        topProduct.getName() + " is showing strong sales momentum with " +
                                String.format("%.1f", topProduct.getPercentage()) +
                                "% of total revenue. Consider a flash sale to boost revenue further.",
                        "promotion",
                        "high",
                        "Expected 25% increase in " + topProduct.getName() + " sales"
                ));
            }

            if (topProducts.size() > 1) {
                TopProduct secondProduct = topProducts.get(1);
                recommendations.add(new Recommendation(
                        "2",
                        "Bundle Opportunity",
                        "Create a bundle offer combining " + topProduct.getName() + " and " +
                                secondProduct.getName() + " to increase average order value.",
                        "pricing",
                        "medium",
                        "Potential 15% increase in average order value"
                ));
            }
        }

        return recommendations;
    }

    private List<Recommendation> generateRevenueRecommendations(Analytics analytics) {
        List<Recommendation> recommendations = new ArrayList<>();

        if (analytics.getRevenueChange() < 0) {
            recommendations.add(new Recommendation(
                    "3",
                    "Revenue Recovery Strategy",
                    "Revenue has decreased by " + String.format("%.1f", Math.abs(analytics.getRevenueChange())) +
                            "%. Consider implementing promotional campaigns or discounts.",
                    "promotion",
                    "high",
                    "Expected 20% revenue recovery within next hour"
            ));
        } else if (analytics.getRevenueChange() > 50) {
            recommendations.add(new Recommendation(
                    "4",
                    "Capitalize on Momentum",
                    "Revenue is surging with " + String.format("%.1f", analytics.getRevenueChange()) +
                            "% growth. Consider increasing inventory for high-demand products.",
                    "inventory",
                    "medium",
                    "Prevent stockouts and maintain growth trajectory"
            ));
        }

        return recommendations;
    }

    private List<Recommendation> generateSeasonalRecommendations() {
        List<Recommendation> recommendations = new ArrayList<>();
        String[] weatherConditions = {"hot", "cold", "rainy", "sunny"};
        String weather = weatherConditions[random.nextInt(weatherConditions.length)];

        switch (weather) {
            case "hot":
                recommendations.add(new Recommendation(
                        "5",
                        "Hot Weather Promotion",
                        "Current weather conditions favor promoting cooling products and summer accessories.",
                        "seasonal",
                        "medium",
                        "Potential 20% boost in seasonal product sales"
                ));
                break;
            case "cold":
                recommendations.add(new Recommendation(
                        "6",
                        "Cold Weather Strategy",
                        "Weather conditions suggest promoting warm beverages and winter accessories.",
                        "seasonal",
                        "medium",
                        "Expected 18% increase in winter product sales"
                ));
                break;
            case "rainy":
                recommendations.add(new Recommendation(
                        "7",
                        "Rainy Day Specials",
                        "Rainy weather creates opportunities for indoor entertainment and comfort products.",
                        "seasonal",
                        "low",
                        "Potential 12% boost in indoor product categories"
                ));
                break;
            default:
                recommendations.add(new Recommendation(
                        "8",
                        "Optimize Product Mix",
                        "Current conditions are ideal for promoting outdoor and recreational products.",
                        "seasonal",
                        "low",
                        "Expected 10% increase in outdoor product sales"
                ));
        }

        return recommendations;
    }

    /* ========================== AI-BASED ========================== */

    private List<Recommendation> getAIRecommendations(Analytics analytics) {
        try {
            String prompt = createPromptFromAnalytics(analytics);
            String aiResponse = getAIResponse(prompt);
            return parseRecommendations(aiResponse);
        } catch (Exception e) {
            System.err.println("Error getting AI recommendations: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private String createPromptFromAnalytics(Analytics analytics) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("As an e-commerce sales analytics AI, generate 3 data-driven product recommendations in JSON format ")
              .append("with fields: id, title, description, category, priority, expectedOutcome. ")
              .append("Here is the data:\n")
              .append(String.format("- Current revenue: $%.2f\n", analytics.getTotalRevenue()))
              .append(String.format("- Revenue change: %.1f%%\n", analytics.getRevenueChange()))
              .append("- Top products:\n");

        for (TopProduct product : analytics.getTopProducts()) {
            prompt.append(String.format("  - %s: %.1f%% of revenue\n", product.getName(), product.getPercentage()));
        }

        return prompt.toString();
    }

    private String getAIResponse(String prompt) {
        ChatMessage message = new ChatMessage(ChatMessageRole.USER.value(), prompt);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-4")
                .messages(List.of(message))
                .temperature(0.7)
                .maxTokens(500)
                .build();

        return openAiService.createChatCompletion(request)
                .getChoices()
                .get(0)
                .getMessage()
                .getContent();
    }

    private List<Recommendation> parseRecommendations(String jsonResponse) {
        try {
            return objectMapper.readValue(jsonResponse, new TypeReference<List<Recommendation>>() {});
        } catch (Exception e) {
            System.err.println("Failed to parse AI JSON response: " + e.getMessage());
            // Fallback: add raw response
            List<Recommendation> fallback = new ArrayList<>();
            fallback.add(new Recommendation(
                    "ai-fallback",
                    "AI Recommendation",
                    jsonResponse,
                    "ai",
                    "high",
                    "Improved sales performance"
            ));
            return fallback;
        }
    }
}
