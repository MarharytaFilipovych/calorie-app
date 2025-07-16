package com.margosha.kse.calorie_client.config;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "llm")
public class LLMSettings {

    @NotBlank
    private String apiKey;

    @NotBlank
    private String model;

    @NotBlank
    private String url;

    private MaxLimits max = new MaxLimits();

    private double temperature = 0.7;

    @Min(30000)
    private int tokenLiveTimeInMs = 60000;

    @Data
    public static class MaxLimits {
        @Min(1)
        @Max(10000)
        private int tokens = 1000;

        @Min(1)
        @Max(100)
        private int requestsPerMinute = 10;

        @Min(100)
        @Max(10000)
        private int tokensPerMinute = 5000;

        @Min(1)
        private int rounds = 5;
    }
}
