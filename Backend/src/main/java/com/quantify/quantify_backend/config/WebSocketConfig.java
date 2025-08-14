package com.quantify.quantify_backend.config;

// UPDATED: Import path now points to the service package
import com.quantify.quantify_backend.service.RealTimeTradeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * This configuration class enables WebSocket support in the Spring application
 * and registers our custom handler to a specific endpoint.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    // Spring will automatically inject the RealTimeTradeHandler bean we create.
    @Autowired
    private RealTimeTradeHandler realTimeTradeHandler;

    /**
     * Registers the WebSocket handlers to specific URL paths.
     * @param registry The registry to add handlers to.
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // This maps the "/ws/trades" endpoint to our handler.
        // setAllowedOrigins("*") allows connections from any domain, which is useful for development.
        registry.addHandler(realTimeTradeHandler, "/ws/trades").setAllowedOrigins("*");
    }
}