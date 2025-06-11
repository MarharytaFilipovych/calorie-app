package com.margosha.kse.calories.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@Component
@Validated
@ConfigurationProperties(prefix = "rabbit")
public class RabbitSettings {

    @NotBlank
    private String exchangeName = "calories.exchange";

    @NotBlank
    private String queueName = "MoreCalories";

    @NotBlank
    private String routingKey = "record.events";
}
