package com.quantify.quantify_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class RealTimeTradeHandler extends TextWebSocketHandler {

    private final WebSocketService webSocketService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RealTimeTradeHandler(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Client connected: " + session.getId());
        webSocketService.establishFinnhubConnection(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            JsonNode jsonNode = objectMapper.readTree(message.getPayload());
            String type = jsonNode.has("type") ? jsonNode.get("type").asText() : null;

            if ("subscribe".equals(type) && jsonNode.has("symbol")) {
                String symbol = jsonNode.get("symbol").asText();
                System.out.println("Received subscription request for " + symbol + " from client " + session.getId());
                webSocketService.subscribeClientToSymbol(session.getId(), symbol);
            }
            // You can add an "unsubscribe" type here as well
        } catch (Exception e) {
            System.err.println("Error processing client message: " + e.getMessage());
            session.sendMessage(new TextMessage("{\"error\":\"Invalid message format\"}"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("Client disconnected: " + session.getId());
        webSocketService.terminateFinnhubConnection(session.getId());
    }
}