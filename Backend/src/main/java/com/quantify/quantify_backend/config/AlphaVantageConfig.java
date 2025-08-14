package com.quantify.quantify_backend.config;

import com.crazzyghost.alphavantage.AlphaVantage;
import com.crazzyghost.alphavantage.AlphaVantageException;
import com.crazzyghost.alphavantage.Config;
import com.crazzyghost.alphavantage.parameters.Interval;
import com.crazzyghost.alphavantage.parameters.OutputSize;
import com.crazzyghost.alphavantage.timeseries.response.TimeSeriesResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlphaVantageConfig {

    // 1. This injects the API key from your application.properties file
    @Value("${alphavantage.api.key}")
    private String apiKey;

    // 2. The @PostConstruct annotation tells Spring to run this method
    //    automatically, only once, after the bean has been created.
    //    This is the perfect place for a one-time initialization.
    @PostConstruct
    public void initializeAlphaVantage() {
        System.out.println("Initializing Alpha Vantage API...");

        Config cfg = Config.builder()
                .key(apiKey)
                .timeOut(10) // Timeout in seconds
                .build();

        AlphaVantage.api().init(cfg);

        System.out.println("✅ Alpha Vantage API Initialized Successfully!");
    }
}
