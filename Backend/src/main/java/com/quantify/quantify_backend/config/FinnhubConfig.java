package com.quantify.quantify_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * This configuration class sets up a dedicated WebClient bean for interacting
 * with the Finnhub API. Creating a specific bean for each external service
 * is a good practice for managing connections and settings.
 */
@Configuration
public class FinnhubConfig {

    /**
     * Creates and configures a WebClient instance with the base URL for the Finnhub API.
     * This bean can then be injected into any service that needs to make calls to Finnhub.
     *
     * @return A configured WebClient instance.
     */
    @Bean
    public WebClient finnhubWebClient() {
        System.out.println("✅ Finnhub WebClient Initialized.");
        return WebClient.builder()
                .baseUrl("https://finnhub.io/api/v1")
                .build();
    }
}
