package com.margosha.kse.CaloriesConsumer.config;

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
    private String exchangeName;

    @NotBlank
    private String queueName;

    @NotBlank
    private String routingKey;
}
