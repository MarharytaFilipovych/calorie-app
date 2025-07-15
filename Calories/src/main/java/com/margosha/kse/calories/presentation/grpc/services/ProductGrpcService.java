package com.margosha.kse.calories.presentation.grpc.services;

import com.margosha.kse.calories.business.dto.ProductFilterDto;
import com.margosha.kse.calories.business.dto.ProductRequestDto;
import com.margosha.kse.calories.business.dto.ProductResponseDto;
import com.margosha.kse.calories.business.service.IdempotencyService;
import com.margosha.kse.calories.business.service.ProductService;
import com.margosha.kse.calories.presentation.grpc.mapper.CommonGrpcMapper;
import com.margosha.kse.calories.presentation.grpc.mapper.ProductGrpcMapper;
import com.margosha.kse.calories.presentation.model.Pagination;
import com.margosha.kse.calories.proto.*;
import com.margosha.kse.calories.proto.common.BooleanResponse;
import com.margosha.kse.calories.proto.common.Id;
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

    public ProductGrpcService(ProductService productService, ProductGrpcMapper productGrpcMapper,
                              CommonGrpcMapper commonGrpcMapper, IdempotencyService idempotencyService) {
        this.productService = productService;
        this.productGrpcMapper = productGrpcMapper;
        this.commonMapper = commonGrpcMapper;
        this.idempotencyService = idempotencyService;
    }

    @Override
    public void getProducts(GetProductsRequest request, StreamObserver<GetProductsResponse> responseObserver) {
        String name = !request.getName().isEmpty() ? request.getName() : null;
        Pagination pagination = commonMapper.toModel(request.getPagination());
        log.info("Limit: {}. Offset: {}", pagination.getLimit(), pagination.getOffset());
        Page<ProductResponseDto> products = productService.getAll(name, pagination.getLimit(), pagination.getOffset());

        GetProductsResponse response = GetProductsResponse.newBuilder()
                .addAllProducts(products.getContent().stream().map(productGrpcMapper::toProto).toList())
                .setMeta(commonMapper.toProto(products))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getProductById(Id request, StreamObserver<Product> responseObserver) {
        UUID id = commonMapper.stringToUuid(request.getId());
        ProductResponseDto productDto = productService.getById(id);
        responseObserver.onNext(productGrpcMapper.toProto(productDto));
        responseObserver.onCompleted();
    }

    @Override
    public void createProduct(ProductInput request, StreamObserver<Id> responseObserver) {
        ProductRequestDto productDto = productGrpcMapper.toDto(request);
        ProductResponseDto created = productService.create(productDto);
        responseObserver.onNext(Id.newBuilder().setId(created.getId().toString()).build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateProduct(UpdateProductRequest request, StreamObserver<Product> responseObserver) {
        UUID id = commonMapper.stringToUuid(request.getId());
        ProductRequestDto productDto = productGrpcMapper.toDto(request.getInput());
        ProductResponseDto updated = productService.updateProduct(productDto, id);
        responseObserver.onNext(productGrpcMapper.toProto(updated));
        responseObserver.onCompleted();
    }

    @Override
    public void deleteProduct(Id request, StreamObserver<BooleanResponse> responseObserver) {
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
        ProductFilterDto filter = productGrpcMapper.toDto(request);
        log.info("🚀 Starting product streaming with filter - name: '{}', brand: '{}', calories: {}-{}, batch size: {}",
                filter.getName(), filter.getBrandName(), filter.getMinCalories(), filter.getMaxCalories(), filter.getMeasurementUnit());
        CompletableFuture.runAsync(() -> {
            try {
                int batchSize = request.getBatchSize() > 0 ? request.getBatchSize() : 10;
                int currentPage = 0;
                int totalStreamed = 0;
                while (true) {
                    Page<ProductResponseDto> productsPage = productService.getAllWithFilter(filter, batchSize, currentPage + 1);
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

    @Override
    public StreamObserver<CreateProductStreamRequest> createProducts(StreamObserver<CreateProductStreamResponse> responseObserver) {
        log.info("🔄 Create product bidirectional streaming started");

        return new StreamObserver<>() {
            @Override
            public void onNext(CreateProductStreamRequest request) {
                String requestId = request.getRequestId();
                String clientId = request.getClientId();

                log.debug("📨 Received create request: {} from client: {}", requestId, clientId);
                if (idempotencyService.isProcessed(requestId)) {
                    log.info("🔁 Create request {} already processed (idempotent)", requestId);
                    IdempotencyService.ProcessedRequest cached = idempotencyService.getProcessedResult(requestId);
                    CreateProductStreamResponse cachedResponse = buildCreateResponseFromCache(request, cached);
                    responseObserver.onNext(cachedResponse);
                    return;
                }

                try {
                    ProductRequestDto productDto = productGrpcMapper.toDto(request.getProduct());
                    ProductResponseDto created = productService.create(productDto);
                    CreateProductStreamResponse response = CreateProductStreamResponse.newBuilder()
                            .setRequestId(requestId)
                            .setClientId(clientId)
                            .setMessage("Product created successfully")
                            .setProduct(productGrpcMapper.toProto(created))
                            .build();
                    idempotencyService.storeResult(requestId, response, true);
                    responseObserver.onNext(response);
                    log.debug("✅ Product created: {} for request: {}", created.getName(), requestId);
                } catch (Exception e) {
                    log.error("❌ Error creating product for request: {}", requestId, e);
                    CreateProductStreamResponse errorResponse = CreateProductStreamResponse.newBuilder()
                            .setRequestId(requestId)
                            .setClientId(clientId)
                            .setMessage("Error: " + e.getMessage())
                            .build();
                    idempotencyService.storeResult(requestId, errorResponse, false);
                    responseObserver.onNext(errorResponse);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("❌ Create product stream error", throwable);
            }

            @Override
            public void onCompleted() {
                log.info("✅ Create product stream completed");
                responseObserver.onCompleted();
            }
        };
    }

    private CreateProductStreamResponse buildCreateResponseFromCache(CreateProductStreamRequest request, IdempotencyService.ProcessedRequest cached) {
        if (cached.getResult() instanceof CreateProductStreamResponse) return (CreateProductStreamResponse) cached.getResult();
        return CreateProductStreamResponse.newBuilder()
                .setRequestId(request.getRequestId())
                .setClientId(request.getClientId())
                .setMessage("Cached result")
                .build();
    }
}