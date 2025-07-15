package com.margosha.kse.calories.config;

import com.margosha.kse.calories.proto.BrandServiceGrpc;
import com.margosha.kse.calories.proto.ProductServiceGrpc;
import com.margosha.kse.calories.proto.RecordServiceGrpc;
import com.margosha.kse.calories.proto.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class GrpcClientConfig {

    @Value("${grpc-deadline-seconds}")
    private long deadline;

    @Bean
    public ManagedChannel grpcChannel(@Value("${grpc.server.address}") String host, @Value("${grpc.server.port}") int port) {
        log.info("Creating gRPC channel to {}:{}", host, port);
        return ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
    }

    @Bean
    public UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub(ManagedChannel managedChannel){
        return UserServiceGrpc.newBlockingStub(managedChannel)
                .withDeadlineAfter(deadline, TimeUnit.SECONDS);
    }

    @Bean
    public ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub(ManagedChannel managedChannel){
        return ProductServiceGrpc.newBlockingStub(managedChannel)
                .withDeadlineAfter(deadline, TimeUnit.SECONDS);
    }

    @Bean
    public RecordServiceGrpc.RecordServiceBlockingStub recordServiceBlockingStub(ManagedChannel managedChannel){
        return RecordServiceGrpc.newBlockingStub(managedChannel)
                .withDeadlineAfter(deadline, TimeUnit.SECONDS);
    }

    @Bean
    public BrandServiceGrpc.BrandServiceBlockingStub brandServiceBlockingStub(ManagedChannel managedChannel){
        return BrandServiceGrpc.newBlockingStub(managedChannel)
                .withDeadlineAfter(deadline, TimeUnit.SECONDS);
    }

    @Bean
    public ProductServiceGrpc.ProductServiceStub productServiceStub(ManagedChannel managedChannel){
        return ProductServiceGrpc.newStub(managedChannel)
                .withDeadlineAfter(deadline, TimeUnit.SECONDS);
    }
}
