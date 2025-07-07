    package com.salesanalytics.service;

    import com.salesanalytics.dto.Analytics;
    import com.salesanalytics.dto.Recommendation;
    import com.salesanalytics.dto.TopProduct;
    import org.springframework.stereotype.Service;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.Random;

    @Service
    public class RecommendationService {
        
        private final OrderService orderService;
        private final Random random = new Random();
        
        public RecommendationService(OrderService orderService) {
            this.orderService = orderService;
        }
        
        public List<Recommendation> getRecommendations() {
            Analytics analytics = orderService.getAnalytics();
            List<Recommendation> recommendations = new ArrayList<>();
            
            // Generate AI-like recommendations based on sales data
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
                        String.format("%.1f", topProduct.getPercentage()) + "% of total revenue. Consider a flash sale to boost revenue further.",
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
                        "Create a bundle offer combining " + topProduct.getName() + " and " + secondProduct.getName() + 
                        " to increase average order value.",
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
                    "% in the last minute. Consider implementing promotional campaigns or discounts.",
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
            
            // Simulate weather-based recommendations
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
    }