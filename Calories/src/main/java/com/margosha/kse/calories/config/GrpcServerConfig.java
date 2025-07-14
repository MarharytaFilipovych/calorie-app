package com.margosha.kse.calories.config;

import io.grpc.protobuf.services.ProtoReflectionService;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.serverfactory.GrpcServerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class GrpcServerConfig {

    @Bean
    public GrpcServerConfigurer grpcServerConfigurer() {
        return serverBuilder -> {
            log.info("🔧 Configuring gRPC server with reflection service...");
            serverBuilder.addService(ProtoReflectionService.newInstance());
            log.info("✅ gRPC server configured with reflection service on port 9090");
        };
    }
}