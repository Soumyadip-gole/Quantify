package com.quantify.quantify_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This service contains the core business logic for handling real-time data streams.
 * It connects to Finnhub and forwards messages to the appropriate client.
 */
@Service
public class WebSocketService {

    @Value("${finnhub.api.key}")
    private String finnhubApiKey;

    // A map to keep track of the Finnhub session for each client session.
    private final Map<String, WebSocketSession> clientToFinnhubSession = new ConcurrentHashMap<>();

    /**
     * Establishes a new WebSocket connection to Finnhub for a given client session.
     * @param clientSession The session of the frontend client.
     */
    public void establishFinnhubConnection(WebSocketSession clientSession) {
        WebSocketClient finnhubClient = new StandardWebSocketClient();
        String finnhubUrl = "wss://ws.finnhub.io?token=" + finnhubApiKey;

        try {
            // This handler will process messages received FROM Finnhub.
            TextWebSocketHandler finnhubMessageHandler = new TextWebSocketHandler() {
                @Override
                public void afterConnectionEstablished(WebSocketSession finnhubSession) {
                    System.out.println("✅ Successfully connected to Finnhub for client: " + clientSession.getId());
                    clientToFinnhubSession.put(clientSession.getId(), finnhubSession);
                    // Hardcoded subscriptions are removed from here.
                }

                @Override
                protected void handleTextMessage(WebSocketSession finnhubSession, TextMessage message) throws IOException {
                    // When a message is received from Finnhub, forward it to our client.
                    if (clientSession.isOpen()) {
                        clientSession.sendMessage(message);
                    }
                }

                @Override
                public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
                    System.out.println("Disconnected from Finnhub for client: " + clientSession.getId());
                    clientToFinnhubSession.remove(clientSession.getId());
                }
            };

            // Initiate the connection to Finnhub.
            finnhubClient.execute(finnhubMessageHandler, String.valueOf(new URI(finnhubUrl)));

        } catch (Exception e) {
            System.err.println("Error connecting to Finnhub: " + e.getMessage());
            try {
                clientSession.close(CloseStatus.SERVER_ERROR.withReason("Failed to connect to data provider."));
            } catch (IOException ioException) {
                // Ignore
            }
        }
    }

    /**
     * Subscribes a specific client to a stock symbol.
     * @param clientSessionId The ID of the client session.
     * @param symbol The stock symbol to subscribe to (e.g., "AAPL").
     */
    public void subscribeClientToSymbol(String clientSessionId, String symbol) throws IOException {
        WebSocketSession finnhubSession = clientToFinnhubSession.get(clientSessionId);
        if (finnhubSession != null && finnhubSession.isOpen()) {
            subscribeToSymbol(finnhubSession, symbol);
        } else {
            System.err.println("Finnhub session not found or closed for client: " + clientSessionId);
        }
    }

    /**
     * Cleans up the Finnhub connection when a client disconnects.
     * @param clientSessionId The ID of the disconnected client session.
     */
    public void terminateFinnhubConnection(String clientSessionId) throws IOException {
        WebSocketSession finnhubSession = clientToFinnhubSession.remove(clientSessionId);
        if (finnhubSession != null && finnhubSession.isOpen()) {
            finnhubSession.close();
        }
    }

    /**
     * Helper method to send a subscription message to Finnhub.
     */
    private void subscribeToSymbol(WebSocketSession finnhubSession, String symbol) throws IOException {
        String subscribeMessage = String.format("{\"type\":\"subscribe\",\"symbol\":\"%s\"}", symbol);
        finnhubSession.sendMessage(new TextMessage(subscribeMessage));
        System.out.println("Sent subscription request for " + symbol + " to Finnhub.");
    }
}