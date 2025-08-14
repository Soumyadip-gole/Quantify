package com.quantify.quantify_backend.controller;

import com.crazzyghost.alphavantage.AlphaVantage;
import com.crazzyghost.alphavantage.AlphaVantageException;
import com.crazzyghost.alphavantage.parameters.OutputSize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/market") // Using /api prefix is a good practice
public class marketData { // Renamed class to follow Java conventions

    /**
     * Fetches time series data for a given stock symbol.
     * Refactored to use GET with @RequestParam, which is the standard for fetching data.
     * Example URL: /api/market/quote?symbol=RELIANCE.BSE&interval=daily
     *
     * @param symbol The stock symbol (e.g., "IBM", "RELIANCE.BSE").
     * @param interval The time interval (e.g., "daily", "weekly", "monthly").
     * @return A CompletableFuture with the API response.
     */
    @GetMapping("/quote")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> getQuote(
            @RequestParam String symbol,
            @RequestParam(defaultValue = "daily") String interval) {

        if (symbol == null || symbol.isEmpty()) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body(createErrorResponse("Symbol is required"))
            );
        }

        CompletableFuture<ResponseEntity<Map<String, Object>>> future = new CompletableFuture<>();

        try {
            // The AlphaVantage library calls are asynchronous, which is handled correctly here.
            switch (interval.toLowerCase()) { // Use toLowerCase for case-insensitivity
                case "weekly":
                    AlphaVantage.api()
                            .timeSeries()
                            .weekly()
                            .forSymbol(symbol)
                            .onSuccess(response -> future.complete(ResponseEntity.ok(createSuccessResponse(symbol, interval, response))))
                            .onFailure(error -> future.complete(createFailureResponse(error)))
                            .fetch();
                    break;
                case "monthly":
                    AlphaVantage.api()
                            .timeSeries()
                            .monthly()
                            .forSymbol(symbol)
                            .onSuccess(response -> future.complete(ResponseEntity.ok(createSuccessResponse(symbol, interval, response))))
                            .onFailure(error -> future.complete(createFailureResponse(error)))
                            .fetch();
                    break;
                default: // "daily"
                    AlphaVantage.api()
                            .timeSeries()
                            .daily()
                            .forSymbol(symbol)
                            .outputSize(OutputSize.COMPACT) // COMPACT is better for recent data, FULL for history
                            .onSuccess(response -> future.complete(ResponseEntity.ok(createSuccessResponse(symbol, interval, response))))
                            .onFailure(error -> future.complete(createFailureResponse(error)))
                            .fetch();
                    break;
            }
        } catch (Exception e) {
            future.complete(
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(createErrorResponse("Failed to initiate data fetch: " + e.getMessage()))
            );
        }

        return future;
    }
    // --- Private Helper Methods ---

    /**
     * Creates a standardized success response body.
     */
    private Map<String, Object> createSuccessResponse(String symbol, String interval, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("symbol", symbol);
        response.put("interval", interval);
        response.put("data", data);
        return response;
    }

    /**
     * Creates a standardized error response body.
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", message);
        return response;
    }

    /**
     * Creates a standardized failure response with HTTP status for AlphaVantage API failures.
     */
    private ResponseEntity<Map<String, Object>> createFailureResponse(AlphaVantageException error) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "API request failed: " + error.getMessage());
        response.put("errorType", "API_FAILURE");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // Worldwide indices with Yahoo Finance symbols - these return ACTUAL index values
    private static final List<Map<String, String>> YAHOO_WORLD_INDICES = List.of(
            // Indian Indices
            Map.of("symbol", "^NSEI", "name", "NIFTY 50", "region", "India"),
            Map.of("symbol", "^BSESN", "name", "BSE SENSEX", "region", "India"),
            Map.of("symbol", "^NSEBANK", "name", "NIFTY Bank", "region", "India"),

            // US Indices
            Map.of("symbol", "^GSPC", "name", "S&P 500", "region", "USA"),
            Map.of("symbol", "^IXIC", "name", "NASDAQ Composite", "region", "USA"),
            Map.of("symbol", "^DJI", "name", "Dow Jones Industrial Average", "region", "USA"),
            Map.of("symbol", "^RUT", "name", "Russell 2000", "region", "USA"),
            Map.of("symbol", "^VIX", "name", "CBOE Volatility Index", "region", "USA"),

            // European Indices
            Map.of("symbol", "^FTSE", "name", "FTSE 100", "region", "UK"),
            Map.of("symbol", "^GDAXI", "name", "DAX PERFORMANCE-INDEX", "region", "Germany"),
            Map.of("symbol", "^FCHI", "name", "CAC 40", "region", "France"),
            Map.of("symbol", "^STOXX50E", "name", "EURO STOXX 50", "region", "Europe"),

            // Asian Indices
            Map.of("symbol", "^N225", "name", "Nikkei 225", "region", "Japan"),
            Map.of("symbol", "^HSI", "name", "HANG SENG INDEX", "region", "Hong Kong"),
            Map.of("symbol", "000001.SS", "name", "SSE Composite Index", "region", "China"),
            Map.of("symbol", "^KS11", "name", "KOSPI Composite Index", "region", "South Korea"),
            Map.of("symbol", "^TWII", "name", "TSEC weighted index", "region", "Taiwan"),
            Map.of("symbol", "^AXJO", "name", "S&P/ASX 200", "region", "Australia"),

            // Other Global Indices
            Map.of("symbol", "^GSPTSE", "name", "S&P/TSX Composite index", "region", "Canada"),
            Map.of("symbol", "^BVSP", "name", "IBOVESPA", "region", "Brazil")
    );

    private final WebClient webClient = WebClient.builder().build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Fetches the recent intraday time series data for the top 20 world indices.
     * This endpoint makes parallel calls to Alpha Vantage to be efficient.
     *
     * @return A CompletableFuture containing a list of indices with their chart data.
     */
    @GetMapping("/indices")
    public CompletableFuture<ResponseEntity<List<Map<String, Object>>>> getIndices() {

        // Create a list of CompletableFuture tasks, one for each index.
        List<CompletableFuture<Map<String, Object>>> futures = YAHOO_WORLD_INDICES.stream()
                .map(this::fetchIndexData)
                .collect(Collectors.toList());

        // Create a single CompletableFuture that completes when all individual futures are done.
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        // When all futures are complete, process their results.
        return allFutures.thenApply(v -> {
            List<Map<String, Object>> results = futures.stream()
                    .map(CompletableFuture::join) // Get the result of each future
                    .filter(result -> !result.isEmpty()) // Filter out any that failed
                    .collect(Collectors.toList());
            return ResponseEntity.ok(results);
        });
    }

    /**
     * Fetches and processes data for a single index symbol using Yahoo Finance API.
     *
     * @param indexInfo A map containing the symbol, name, and region of the index.
     * @return A CompletableFuture that will contain the processed data for the index.
     */
    private CompletableFuture<Map<String, Object>> fetchIndexData(Map<String, String> indexInfo) {
        String symbol = indexInfo.get("symbol");
        String name = indexInfo.get("name");
        String region = indexInfo.get("region");

        return fetchYahooFinanceData(symbol)
                .thenApply(data -> processYahooResponse(symbol, name, region, data))
                .exceptionally(throwable -> {
                    System.err.println("Failed to fetch data for " + symbol + ": " + throwable.getMessage());
                    return Collections.emptyMap();
                });
    }

    /**
     * Fetches data from Yahoo Finance API
     */
    private CompletableFuture<String> fetchYahooFinanceData(String symbol) {
        String yahooUrl = String.format(
            "https://query1.finance.yahoo.com/v8/finance/chart/%s?interval=1d&range=30d",
            symbol
        );

        return webClient.get()
                .uri(yahooUrl)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .retrieve()
                .bodyToMono(String.class)
                .toFuture();
    }

    /**
     * Processes Yahoo Finance response into standardized format
     */
    private Map<String, Object> processYahooResponse(String symbol, String name, String region, String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode chart = root.path("chart").path("result").get(0);

            if (chart == null || chart.isMissingNode()) {
                return Collections.emptyMap();
            }

            JsonNode meta = chart.path("meta");
            JsonNode timestamps = chart.path("timestamp");
            JsonNode quotes = chart.path("indicators").path("quote").get(0);

            if (meta.isMissingNode() || timestamps.isMissingNode() || quotes.isMissingNode()) {
                return Collections.emptyMap();
            }

            // Get current price and previous close
            double currentPrice = meta.path("regularMarketPrice").asDouble(0.0);
            double previousClose = meta.path("previousClose").asDouble(0.0);

            // Calculate change and percentage change
            double change = currentPrice - previousClose;
            double percentChange = previousClose != 0 ? (change / previousClose) * 100 : 0.0;

            // Build chart data from historical prices
            List<Map<String, Object>> chartData = new ArrayList<>();
            JsonNode closeArray = quotes.path("close");

            for (int i = 0; i < timestamps.size() && i < closeArray.size(); i++) {
                long timestamp = timestamps.get(i).asLong();
                double close = closeArray.get(i).asDouble(0.0);

                if (close > 0) { // Only add valid price points
                    Map<String, Object> dataPoint = new HashMap<>();
                    dataPoint.put("time", timestamp);
                    dataPoint.put("value", close);
                    chartData.add(dataPoint);
                }
            }

            // Return formatted response
            Map<String, Object> result = new HashMap<>();
            result.put("symbol", symbol);
            result.put("name", name);
            result.put("region", region);
            result.put("price", currentPrice);
            result.put("change", change);
            result.put("percentChange", percentChange);
            result.put("currency", meta.path("currency").asText("USD"));
            result.put("marketState", meta.path("marketState").asText("UNKNOWN"));
            result.put("chartData", chartData);
            result.put("lastUpdated", System.currentTimeMillis());

            return result;

        } catch (Exception e) {
            System.err.println("Error processing Yahoo Finance response for " + symbol + ": " + e.getMessage());
            return Collections.emptyMap();
        }
    }

}
