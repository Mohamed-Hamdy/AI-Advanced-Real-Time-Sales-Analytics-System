package com.salesanalytics.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import com.salesanalytics.websocket.SalesWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final SalesWebSocketHandler salesWebSocketHandler;

    public WebSocketConfig(SalesWebSocketHandler salesWebSocketHandler) {
        this.salesWebSocketHandler = salesWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(salesWebSocketHandler, "/ws")
                .setAllowedOrigins("http://localhost:5173", "http://localhost:3000");
    }
}