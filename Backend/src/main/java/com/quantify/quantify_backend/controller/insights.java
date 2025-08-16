package com.quantify.quantify_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/insights")
public class insights {

    @Value("${alphavantage.api.key}")
    private String alphaVantageApiKey;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @GetMapping("/get")
    public Map<String, Object> getInsights(@RequestParam String symbol) {
        if (symbol == null || symbol.isEmpty()) {
            System.out.println("Symbol parameter is required");
        }
        String quote = alphavantage_get_quote(symbol);
        Map<String,String> insights = geminicall(quote);
        Map<String,String> company = geminicall2(symbol);
        Map<String, Object> response = new HashMap<>();
        response.put("about", company);
        response.put("insights", insights);

        return response;
    }

    public String alphavantage_get_quote(String symbol) {
        // Create a WebClient instance to make HTTP requests
        WebClient webClient = WebClient.create();
        return webClient.get()
                .uri("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol="
                        + symbol + "&apikey=" + alphaVantageApiKey)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public Map<String, String> geminicall(String quote) {
        // Build a simple prompt to get stock insight from the Alpha Vantage GLOBAL_QUOTE JSON
        String prompt =
                "You are a financial analysis assistant. Using ONLY the Alpha Vantage GLOBAL_QUOTE JSON below, " +
                        "write a concise stock insight. Cover: current price, absolute and percentage change, intraday momentum/trend, " +
                        "volume context if available, short‑term outlook, and any cautionary notes. Keep it under 120 words. " +
                        "If any field is missing, say 'data unavailable'.\n\n" +
                        "GLOBAL_QUOTE JSON:\n" + quote;

        Client client = Client.builder().apiKey(geminiApiKey).build();

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        prompt,
                        null);

        String insightText = response != null ? response.text() : "";

        Map<String, String> insights = new HashMap<>();
        insights.put("insight", insightText);
        return insights;
    }

    public Map<String, String> geminicall2(String symbol) {
        // Build a simple prompt to get stock insight from the Alpha Vantage GLOBAL_QUOTE JSON
        String prompt =
                "Give a very short overview about the Company and its business from its symbol " +
                        "symbol:\n" + symbol;

        Client client = Client.builder().apiKey(geminiApiKey).build();

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        prompt,
                        null);

        String insightText = response != null ? response.text() : "";

        Map<String, String> result = new HashMap<>();
        result.put("company", insightText);
        return result;
    }
}