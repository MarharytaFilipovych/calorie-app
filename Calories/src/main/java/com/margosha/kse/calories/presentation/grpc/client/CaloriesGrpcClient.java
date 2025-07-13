package com.margosha.kse.calories.presentation.grpc.client;

import com.margosha.kse.calories.proto.*;
import com.margosha.kse.calories.proto.Record;
import com.margosha.kse.calories.proto.common.IdRequest;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CaloriesGrpcClient {

    private final UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;
    private final ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub;
    private final BrandServiceGrpc.BrandServiceBlockingStub brandServiceBlockingStub;
    private final RecordServiceGrpc.RecordServiceBlockingStub recordServiceBlockingStub;
    private final ProductServiceGrpc.ProductServiceStub productServiceStub;

    public CaloriesGrpcClient(UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub,
                              ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub,
                              BrandServiceGrpc.BrandServiceBlockingStub brandServiceBlockingStub,
                              RecordServiceGrpc.RecordServiceBlockingStub recordServiceBlockingStub,
                              ProductServiceGrpc.ProductServiceStub productServiceStub) {
        this.userServiceBlockingStub = userServiceBlockingStub;
        this.productServiceBlockingStub = productServiceBlockingStub;
        this.brandServiceBlockingStub = brandServiceBlockingStub;
        this.recordServiceBlockingStub = recordServiceBlockingStub;
        this.productServiceStub = productServiceStub;
    }

    public User getUserById(String userId) {
        IdRequest request = IdRequest.newBuilder()
                .setId(userId)
                .build();
        return userServiceBlockingStub.getUserById(request);
    }

    public User createUser(UserInput userInput) {
        CreateUserRequest request = CreateUserRequest.newBuilder()
                .setInput(userInput)
                .build();
        CreateUserResponse response = userServiceBlockingStub.createUser(request);
        return getUserById(response.getId());
    }

    public GetUsersResponse getAllUsers(int limit, int offset) {
        GetUsersRequest request = GetUsersRequest.newBuilder()
                .setPagination(
                        com.margosha.kse.calories.proto.common.Pagination.newBuilder()
                                .setLimit(limit)
                                .setOffset(offset)
                                .build()
                )
                .build();
        return userServiceBlockingStub.getUsers(request);
    }

    public Product getProductById(String productId) {
        IdRequest request = IdRequest.newBuilder()
                .setId(productId)
                .build();
        return productServiceBlockingStub.getProductById(request);
    }

    public GetProductsResponse getProducts(String name, int limit, int offset) {
        GetProductsRequest.Builder requestBuilder = GetProductsRequest.newBuilder()
                .setPagination(
                        com.margosha.kse.calories.proto.common.Pagination.newBuilder()
                                .setLimit(limit)
                                .setOffset(offset)
                                .build()
                );

        if (name != null && !name.isEmpty()) {
            requestBuilder.setName(name);
        }

        return productServiceBlockingStub.getProducts(requestBuilder.build());
    }

    public Product createProduct(ProductInput productInput) {
        CreateProductRequest request = CreateProductRequest.newBuilder()
                .setInput(productInput)
                .build();
        CreateProductResponse response = productServiceBlockingStub.createProduct(request);
        return getProductById(response.getId());
    }

    public Brand getBrandById(String brandId) {
        IdRequest request = IdRequest.newBuilder()
                .setId(brandId)
                .build();
        return brandServiceBlockingStub.getBrandById(request);
    }

    public Brand createBrand(BrandInput brandInput) {
        CreateBrandRequest request = CreateBrandRequest.newBuilder()
                .setInput(brandInput)
                .build();
        return brandServiceBlockingStub.createBrand(request);
    }

    public GetAllBrandsResponse getAllBrands(int limit, int offset) {
        GetAllBrandsRequest request = GetAllBrandsRequest.newBuilder()
                .setPagination(
                        com.margosha.kse.calories.proto.common.Pagination.newBuilder()
                                .setLimit(limit)
                                .setOffset(offset)
                                .build()
                )
                .build();
        return brandServiceBlockingStub.getAllBrands(request);
    }

    public Record getRecordById(String userId, String recordId) {
        GetRecordRequest request = GetRecordRequest.newBuilder()
                .setUserId(userId)
                .setId(recordId)
                .build();
        return recordServiceBlockingStub.getRecord(request);
    }

    public GetRecordsResponse getRecords(String userId, int limit, int offset) {
        GetRecordsRequest request = GetRecordsRequest.newBuilder()
                .setUserId(userId)
                .setPagination(
                        com.margosha.kse.calories.proto.common.Pagination.newBuilder()
                                .setLimit(limit)
                                .setOffset(offset)
                                .build()
                ).build();
        return recordServiceBlockingStub.getRecords(request);
    }

    public Record createRecord(String userId, RecordInput recordInput) {
        CreateRecordRequest request = CreateRecordRequest.newBuilder()
                .setUserId(userId)
                .setInput(recordInput)
                .build();
        CreateRecordResponse response = recordServiceBlockingStub.createRecord(request);
        return getRecordById(userId, response.getId());
    }

    public void streamProducts(String nameFilter, int batchSize, StreamObserver<Product> responseObserver) {
        log.info("🚀 Starting client-side product streaming with filter: '{}', batch size: {}", nameFilter, batchSize);

        StreamProductsRequest.Builder requestBuilder = StreamProductsRequest.newBuilder()
                .setBatchSize(batchSize);

        if (nameFilter != null && !nameFilter.isEmpty()) requestBuilder.setName(nameFilter);

        productServiceStub.streamProducts(requestBuilder.build(), new StreamObserver<>() {
            private int receivedCount = 0;

            @Override
            public void onNext(Product product) {
                receivedCount++;
                log.debug("📦 Received product: {} (#{}) via streaming", product.getName(), receivedCount);
                responseObserver.onNext(product);
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("❌ Product streaming error after {} products", receivedCount, throwable);
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                log.info("✅ Product streaming completed. Total received: {} products", receivedCount);
                responseObserver.onCompleted();
            }
        });
    }

    public StreamObserver<CreateProductStreamRequest> createProductStream(StreamObserver<CreateProductStreamResponse> responseObserver) {
        log.info("🔄 Starting bidirectional product creation streaming");

        StreamObserver<CreateProductStreamRequest> requestObserver = productServiceStub.mutateProducts(
                new StreamObserver<>() {
                    private int responseCount = 0;

                    @Override
                    public void onNext(CreateProductStreamResponse response) {
                        responseCount++;
                        log.debug("📨 Received create response: {} from server (#{}) - {}",
                                response.getRequestId(), responseCount, response.getMessage());
                        responseObserver.onNext(response);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        log.error("❌ Bidirectional streaming error after {} responses", responseCount, throwable);
                        responseObserver.onError(throwable);
                    }

                    @Override
                    public void onCompleted() {
                        log.info("✅ Bidirectional streaming completed. Total responses: {}", responseCount);
                        responseObserver.onCompleted();
                    }
                }
        );

        return new StreamObserver<>() {
            private int requestCount = 0;

            @Override
            public void onNext(CreateProductStreamRequest request) {
                requestCount++;
                log.debug("📤 Sending create request: {} to server (#{}) from client: {}",
                        request.getRequestId(), requestCount, request.getClientId());
                requestObserver.onNext(request);
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("❌ Client streaming error after {} requests", requestCount, throwable);
                requestObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                log.info("✅ Client streaming completed. Total requests sent: {}", requestCount);
                requestObserver.onCompleted();
            }
        };
    }
}