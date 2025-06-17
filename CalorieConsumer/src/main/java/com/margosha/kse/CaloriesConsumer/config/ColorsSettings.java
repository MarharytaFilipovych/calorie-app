package com.margosha.kse.CaloriesConsumer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:colors.properties")
@ConfigurationProperties(prefix = "color")
@Data
public class ColorsSettings {
    private String red;
    private String blue;
    private String magenta;
    private String reset;
}
