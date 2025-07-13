package com.margosha.kse.calories.presentation.grpc.services;

import com.margosha.kse.calories.business.dto.ProductRequestDto;
import com.margosha.kse.calories.business.dto.ProductResponseDto;
import com.margosha.kse.calories.business.service.ProductService;
import com.margosha.kse.calories.presentation.grpc.mapper.CommonGrpcMapper;
import com.margosha.kse.calories.presentation.grpc.mapper.ProductGrpcMapper;
import com.margosha.kse.calories.proto.*;
import com.margosha.kse.calories.proto.common.BooleanResponse;
import com.margosha.kse.calories.proto.common.IdRequest;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import java.util.UUID;

@GrpcService
public class ProductGrpcService extends ProductServiceGrpc.ProductServiceImplBase {
    private final ProductService productService;
    private final ProductGrpcMapper productGrpcMapper;
    private final CommonGrpcMapper commonMapper;

    public ProductGrpcService(ProductService productService, ProductGrpcMapper productGrpcMapper, CommonGrpcMapper commonMapper) {
        this.productService = productService;
        this.productGrpcMapper = productGrpcMapper;
        this.commonMapper = commonMapper;
    }

    @Override
    public void getProducts(GetProductsRequest request, StreamObserver<GetProductsResponse> responseObserver) {
        String name = request.getName().isEmpty() ? null : request.getName();
        int limit = request.getPagination().getLimit() > 0 ? request.getPagination().getLimit() : 20;
        int offset = request.getPagination().getOffset() > 0 ? request.getPagination().getOffset() : 1;

        Page<ProductResponseDto> products = productService.getAll(name, limit, offset);

        GetProductsResponse response = GetProductsResponse.newBuilder()
                .addAllProducts(products.getContent().stream().map(productGrpcMapper::toProto).toList())
                .setMeta(commonMapper.toProto(products))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getProductById(IdRequest request, StreamObserver<Product> responseObserver) {
        UUID id = commonMapper.stringToUuid(request.getId());
        ProductResponseDto productDto = productService.getById(id);
        responseObserver.onNext(productGrpcMapper.toProto(productDto));
        responseObserver.onCompleted();
    }

    @Override
    public void createProduct(CreateProductRequest request, StreamObserver<CreateProductResponse> responseObserver) {
        ProductRequestDto productDto = productGrpcMapper.fromProtoInput(request.getInput());
        ProductResponseDto created = productService.create(productDto);

        CreateProductResponse response = CreateProductResponse.newBuilder()
                .setId(commonMapper.uuidToString(created.getId()))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateProduct(UpdateProductRequest request, StreamObserver<Product> responseObserver) {
        UUID id = commonMapper.stringToUuid(request.getId());
        ProductRequestDto productDto = productGrpcMapper.fromProtoInput(request.getInput());
        ProductResponseDto updated = productService.updateProduct(productDto, id);

        responseObserver.onNext(productGrpcMapper.toProto(updated));
        responseObserver.onCompleted();
    }

    @Override
    public void deleteProduct(IdRequest request, StreamObserver<BooleanResponse> responseObserver) {
        UUID id = commonMapper.stringToUuid(request.getId());
        boolean deleted = productService.delete(id);

        BooleanResponse response = BooleanResponse.newBuilder()
                .setSuccess(deleted)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}