package com.margosha.kse.calories.presentation.grpc.services;

import com.margosha.kse.calories.business.dto.ProductRequestDto;
import com.margosha.kse.calories.business.dto.ProductResponseDto;
import com.margosha.kse.calories.business.service.IdempotencyService;
import com.margosha.kse.calories.business.service.ProductService;
import com.margosha.kse.calories.presentation.aspects.GrpcServerLoggingAspect;
import com.margosha.kse.calories.presentation.grpc.mapper.CommonGrpcMapper;
import com.margosha.kse.calories.presentation.grpc.mapper.ProductGrpcMapper;
import com.margosha.kse.calories.proto.*;
import com.margosha.kse.calories.proto.common.BooleanResponse;
import com.margosha.kse.calories.proto.common.IdRequest;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@GrpcService
public class ProductGrpcService extends ProductServiceGrpc.ProductServiceImplBase {
    private final ProductService productService;
    private final ProductGrpcMapper productGrpcMapper;
    private final CommonGrpcMapper commonMapper;
    private final IdempotencyService idempotencyService;

    public ProductGrpcService(ProductService productService, ProductGrpcMapper productGrpcMapper, CommonGrpcMapper commonMapper, IdempotencyService idempotencyService) {
        this.productService = productService;
        this.productGrpcMapper = productGrpcMapper;
        this.commonMapper = commonMapper;
        this.idempotencyService = idempotencyService;
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

    @Override
    public void streamProducts(StreamProductsRequest request, StreamObserver<Product> responseObserver) {
        log.info("🚀 Starting product streaming with filter: '{}', batch size: {}",
                request.getName(), request.getBatchSize());
        CompletableFuture.runAsync(() -> {
            try {
                String nameFilter = request.getName().isEmpty() ? null : request.getName();
                int batchSize = request.getBatchSize() > 0 ? request.getBatchSize() : 10;
                int currentPage = 0;
                int totalStreamed = 0;
                while (true) {
                    Page<ProductResponseDto> productsPage = productService.getAll(nameFilter, batchSize, currentPage + 1);
                    List<ProductResponseDto> products = productsPage.getContent();
                    if (products.isEmpty()) {
                        log.info("✅ Product streaming completed. Total products streamed: {}", totalStreamed);
                        responseObserver.onCompleted();
                        break;
                    }
                    for (ProductResponseDto productDto : products) {
                        if (responseObserver instanceof io.grpc.stub.ServerCallStreamObserver) {
                            if (((io.grpc.stub.ServerCallStreamObserver<?>) responseObserver).isCancelled()) {
                                log.warn("⚠️ Client cancelled product streaming at product {}", totalStreamed);
                                return;
                            }
                        }
                        Product product = productGrpcMapper.toProto(productDto);
                        responseObserver.onNext(product);
                        totalStreamed++;
                        log.debug("📦 Streamed product: {} (total: {})", productDto.getName(), totalStreamed);
                    }
                    currentPage++;
                    if (!productsPage.hasNext()) {
                        log.info("✅ Product streaming completed. Total products streamed: {}", totalStreamed);
                        responseObserver.onCompleted();
                        break;
                    }
                }
            } catch (Exception e) {
                log.error("❌ Error during product streaming", e);
                responseObserver.onError(io.grpc.Status.INTERNAL
                        .withDescription("Error during streaming: " + e.getMessage())
                        .asException());
            }
        });
    }

//    @Override
//    public StreamObserver<CreateProductStreamRequest> createProductStream(StreamObserver<CreateProductStreamResponse> responseObserver) {
//        log.info("🔄 Create product bidirectional streaming started");
//
//        return new StreamObserver<CreateProductStreamRequest>() {
//            @Override
//            public void onNext(CreateProductStreamRequest request) {
//                String requestId = request.getRequestId();
//                String clientId = request.getClientId();
//
//                log.debug("📨 Received create request: {} from client: {}", requestId, clientId);
//                if (idempotencyService.isProcessed(requestId)) {
//                    log.info("🔁 Create request {} already processed (idempotent)", requestId);
//                    IdempotencyService.ProcessedRequest cached = idempotencyService.getProcessedResult(requestId);
//                    CreateProductStreamResponse cachedResponse = buildCreateResponseFromCache(request, cached);
//                    responseObserver.onNext(cachedResponse);
//                    return;
//                }
//
//                try {
//                    ProductRequestDto productDto = productGrpcMapper.fromProtoInput(request.getCreate().getInput());
//                    ProductResponseDto created = productService.create(productDto);
//                    CreateProductStreamResponse response = CreateProductStreamResponse.newBuilder()
//                            .setRequestId(requestId)
//                            .setClientId(clientId)
//                            .setMessage("Product created successfully")
//                            .setProduct(productGrpcMapper.toProto(created))
//                            .build();
//                    idempotencyService.storeResult(requestId, response, true);
//                    responseObserver.onNext(response);
//                    log.debug("✅ Product created: {} for request: {}", created.getName(), requestId);
//                } catch (Exception e) {
//                    log.error("❌ Error creating product for request: {}", requestId, e);
//                    CreateProductStreamResponse errorResponse = CreateProductStreamResponse.newBuilder()
//                            .setRequestId(requestId)
//                            .setClientId(clientId)
//                            .setMessage("Error: " + e.getMessage())
//                            .build();
//                    idempotencyService.storeResult(requestId, errorResponse, false);
//                    responseObserver.onNext(errorResponse);
//                }
//            }
//
//            @Override
//            public void onError(Throwable throwable) {
//                log.error("❌ Create product stream error", throwable);
//            }
//
//            @Override
//            public void onCompleted() {
//                log.info("✅ Create product stream completed");
//                responseObserver.onCompleted();
//            }
//        };
//    }
//
//    private CreateProductStreamResponse buildCreateResponseFromCache(CreateProductStreamRequest request, IdempotencyService.ProcessedRequest cached) {
//        if (cached.getResult() instanceof CreateProductStreamResponse) return (CreateProductStreamResponse) cached.getResult();
//        return CreateProductStreamResponse.newBuilder()
//                .setRequestId(request.getRequestId())
//                .setClientId(request.getClientId())
//                .setMessage("Cached result")
//                .build();
//    }
}