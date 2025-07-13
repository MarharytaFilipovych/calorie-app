package com.margosha.kse.calories.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "grpc.client")
public class GrpcClientSettings {
    private int port;
    private String host;
    private long deadline;
    private long keepAliveTime;
    private long keepAliveTimeout;
    private int maxInboundMessageSize;
}
