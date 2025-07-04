package com.analytics.salessystem.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class RecommendationService {

    // Mocked for now — implement OpenAI integration later
    public String getOpenAIRecommendation() {
        return "Promote high-selling products like 'Coffee Deluxe'. Consider bundling.";
    }

    public String getWeatherAdvice() {
        String apiKey = "YOUR_KEY"
		String url = "https://api.openweathermap.org/data/2.5/weather?q=Cairo&appid=" + apiKey + "&units=metric";

        RestTemplate restTemplate = new RestTemplate();
        
        try {
            Map response = restTemplate.getForObject(url, Map.class);
            Map<String, Object> main = (Map<String, Object>) response.get("main");
            Double temp = (Double) main.get("temp");

            if (temp >= 30) return "It's hot — promote cold drinks.";
            else if (temp <= 15) return "It's cold — promote hot drinks.";
            else return "Weather is moderate — promote seasonal items.";
        } catch (Exception e) {
            return "Weather data unavailable.";
        }
    }
}
