package com.salesanalytics.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesanalytics.dto.Analytics;
import com.salesanalytics.dto.OrderResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class SalesWebSocketHandler implements WebSocketHandler {
    
    private final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("WebSocket connection established: " + session.getId());
        
        // Send welcome message
        WebSocketMessage welcomeMessage = new WebSocketMessage("connected", "WebSocket connection established");
        sendMessage(session, welcomeMessage);
    }

    @Override
    public void handleMessage(WebSocketSession session, org.springframework.web.socket.WebSocketMessage<?> message) throws Exception {
        System.out.println("Received message: " + message.getPayload());

    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("WebSocket transport error: " + exception.getMessage());
        sessions.remove(session);
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        sessions.remove(session);
        System.out.println("WebSocket connection closed: " + session.getId());
    }
    
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
    
    public void broadcastNewOrder(OrderResponse order) {
        WebSocketMessage message = new WebSocketMessage("new_order", order);
        broadcast(message);
    }
    
    public void broadcastAnalyticsUpdate(Analytics analytics) {
        WebSocketMessage message = new WebSocketMessage("analytics_update", analytics);
        broadcast(message);
    }
    
    private void broadcast(WebSocketMessage message) {
        sessions.removeIf(session -> {
            try {
                if (session.isOpen()) {
                    sendMessage(session, message);
                    return false;
                } else {
                    return true;
                }
            } catch (Exception e) {
                System.err.println("Error broadcasting message: " + e.getMessage());
                return true;
            }
        });
    }
    
    private void sendMessage(WebSocketSession session, WebSocketMessage message) throws IOException {
        String json = objectMapper.writeValueAsString(message);
        session.sendMessage(new TextMessage(json));
    }
    
    public static class WebSocketMessage {
        private String type;
        private Object data;
        
        public WebSocketMessage() {}
        
        public WebSocketMessage(String type, Object data) {
            this.type = type;
            this.data = data;
        }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
    }
}