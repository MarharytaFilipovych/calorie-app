package com.margosha.kse.calorie_client.config;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "guard")
public class PromptInjectionGuardSettings {

    private List<String> suspiciousPatterns;

    private Regex regex = new Regex();

    private Thresholds thresholds = new Thresholds();

    private Map<String, Double> weights = new HashMap<>();

    @Data
    public static class Regex {
        @NotBlank
        private String commandInjection;

        @NotBlank
        private String roleInjection;
    }

    @Data
    public static class Thresholds {
        @DecimalMin("0.0")
        @DecimalMax("1.0")
        private double safeRiskScoreBoundary;

        @Min(100)
        private int lengthLimit;

        private Repetition repetition = new Repetition();

        @Data
        public static class Repetition {
            @Min(10)
            private int minLength;

            @Min(5)
            private int chunkSize;

            @Min(1)
            private int maxCount;
        }
    }
}
