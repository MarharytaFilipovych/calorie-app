package com.margosha.kse.calories.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class GrpcClientConfig {

    private final GrpcClientSettings settings;

    public GrpcClientConfig(GrpcClientSettings settings) {
        this.settings = settings;
    }

    @Bean
    public ManagedChannel managedChannel() {
        log.info("Creating gRPC channel to {}:{}", settings.getHost(), settings.getPort());
        return ManagedChannelBuilder.forAddress(settings.getHost(), settings.getPort())
                .usePlaintext()
                .keepAliveTime(settings.getKeepAliveTime(), TimeUnit.SECONDS)
                .keepAliveTimeout(settings.getKeepAliveTimeout(), TimeUnit.SECONDS)
                .keepAliveWithoutCalls(true)
                .maxInboundMessageSize(settings.getMaxInboundMessageSize())
                .build();
    }
}
