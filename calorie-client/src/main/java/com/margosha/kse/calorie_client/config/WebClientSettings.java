package com.margosha.kse.calorie_client.config;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.time.DurationMax;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.time.Duration;

@ConfigurationProperties(prefix = "calories.client")
@Data
@Component
public class WebClientSettings {

    @NotBlank
    @URL
    private String baseUrl;

    @Min(0)
    @Max(10)
    private int maxRetries;

    @DurationMin(millis = 100)
    private Duration retryDelay;

    @DurationMin(seconds = 1)
    private Duration maxRetryDelay;

    @DecimalMin("1.0")
    @DecimalMax("10.0")
    private double retryMultiplier;

    @DurationMin(seconds = 1)
    @DurationMax(minutes = 5)
    private Duration timeout;

    @DurationMin(millis = 500)
    private Duration connectionTimeout;
}
