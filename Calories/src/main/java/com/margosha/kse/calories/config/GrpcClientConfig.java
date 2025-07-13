package com.margosha.kse.calories.config;

import com.margosha.kse.calories.proto.BrandServiceGrpc;
import com.margosha.kse.calories.proto.ProductServiceGrpc;
import com.margosha.kse.calories.proto.RecordServiceGrpc;
import com.margosha.kse.calories.proto.UserServiceGrpc;
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

    @Bean
    public UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub(ManagedChannel managedChannel){
        return UserServiceGrpc.newBlockingStub(managedChannel)
                .withDeadlineAfter(settings.getDeadline(), TimeUnit.SECONDS);
    }

    @Bean
    public ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub(ManagedChannel managedChannel){
        return ProductServiceGrpc.newBlockingStub(managedChannel)
                .withDeadlineAfter(settings.getDeadline(), TimeUnit.SECONDS);
    }

    @Bean
    public RecordServiceGrpc.RecordServiceBlockingStub recordServiceBlockingStub(ManagedChannel managedChannel){
        return RecordServiceGrpc.newBlockingStub(managedChannel)
                .withDeadlineAfter(settings.getDeadline(), TimeUnit.SECONDS);
    }

    @Bean
    public BrandServiceGrpc.BrandServiceBlockingStub brandServiceBlockingStub(ManagedChannel managedChannel){
        return BrandServiceGrpc.newBlockingStub(managedChannel)
                .withDeadlineAfter(settings.getDeadline(), TimeUnit.SECONDS);
    }

    @Bean
    public ProductServiceGrpc.ProductServiceStub productServiceStub(ManagedChannel managedChannel){
        return ProductServiceGrpc.newStub(managedChannel)
                .withDeadlineAfter(settings.getDeadline(), TimeUnit.SECONDS);
    }
}
