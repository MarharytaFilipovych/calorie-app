package com.margosha.kse.calories.presentation.grpc.client;

import com.margosha.kse.calories.config.GrpcClientSettings;
import com.margosha.kse.calories.proto.*;
import com.margosha.kse.calories.proto.Record;
import com.margosha.kse.calories.proto.common.IdRequest;
import io.grpc.ManagedChannel;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class CaloriesGrpcClient {

    private final UserServiceGrpc.UserServiceBlockingStub userStub;
    private final ProductServiceGrpc.ProductServiceBlockingStub productStub;
    private final BrandServiceGrpc.BrandServiceBlockingStub brandStub;
    private final RecordServiceGrpc.RecordServiceBlockingStub recordStub;

    public CaloriesGrpcClient(ManagedChannel channel, GrpcClientSettings settings) {
        long deadlineSeconds = settings.getDeadline();

        this.userStub = UserServiceGrpc.newBlockingStub(channel)
                .withDeadlineAfter(deadlineSeconds, TimeUnit.SECONDS);
        
        this.productStub = ProductServiceGrpc.newBlockingStub(channel)
                .withDeadlineAfter(deadlineSeconds, TimeUnit.SECONDS);
        
        this.brandStub = BrandServiceGrpc.newBlockingStub(channel)
                .withDeadlineAfter(deadlineSeconds, TimeUnit.SECONDS);
        
        this.recordStub = RecordServiceGrpc.newBlockingStub(channel)
                .withDeadlineAfter(deadlineSeconds, TimeUnit.SECONDS);
    }

    public User getUserById(String userId) {
        IdRequest request = IdRequest.newBuilder()
                .setId(userId)
                .build();
        return userStub.getUserById(request);
    }

    public User createUser(UserInput userInput) {
        CreateUserRequest request = CreateUserRequest.newBuilder()
                .setInput(userInput)
                .build();
        CreateUserResponse response = userStub.createUser(request);
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
        return userStub.getUsers(request);
    }

    public Product getProductById(String productId) {
        IdRequest request = IdRequest.newBuilder()
                .setId(productId)
                .build();
        return productStub.getProductById(request);
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

        return productStub.getProducts(requestBuilder.build());
    }

    public Product createProduct(ProductInput productInput) {
        CreateProductRequest request = CreateProductRequest.newBuilder()
                .setInput(productInput)
                .build();
        CreateProductResponse response = productStub.createProduct(request);
        return getProductById(response.getId());
    }

    public Brand getBrandById(String brandId) {
        IdRequest request = IdRequest.newBuilder()
                .setId(brandId)
                .build();
        return brandStub.getBrandById(request);
    }

    public Brand createBrand(BrandInput brandInput) {
        CreateBrandRequest request = CreateBrandRequest.newBuilder()
                .setInput(brandInput)
                .build();
        return brandStub.createBrand(request);
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
        return brandStub.getAllBrands(request);
    }

    public Record getRecordById(String userId, String recordId) {
        GetRecordRequest request = GetRecordRequest.newBuilder()
                .setUserId(userId)
                .setId(recordId)
                .build();
        return recordStub.getRecord(request);
    }

    public GetRecordsResponse getRecords(String userId, int limit, int offset) {
        GetRecordsRequest request = GetRecordsRequest.newBuilder()
                .setUserId(userId)
                .setPagination(
                        com.margosha.kse.calories.proto.common.Pagination.newBuilder()
                                .setLimit(limit)
                                .setOffset(offset)
                                .build()
                )
                .build();
        return recordStub.getRecords(request);
    }

    public Record createRecord(String userId, RecordInput recordInput) {
        CreateRecordRequest request = CreateRecordRequest.newBuilder()
                .setUserId(userId)
                .setInput(recordInput)
                .build();
        CreateRecordResponse response = recordStub.createRecord(request);
        return getRecordById(userId, response.getId());
    }
}