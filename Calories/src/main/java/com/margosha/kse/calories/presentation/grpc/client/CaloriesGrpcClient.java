package com.margosha.kse.calories.presentation.grpc.client;

import com.margosha.kse.calories.proto.*;
import com.margosha.kse.calories.proto.Record;
import com.margosha.kse.calories.proto.common.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class CaloriesGrpcClient {

    private final UserServiceGrpc.UserServiceBlockingStub userService;
    private final BrandServiceGrpc.BrandServiceBlockingStub brandService;
    private final ProductServiceGrpc.ProductServiceBlockingStub productService;
    private final ProductServiceGrpc.ProductServiceStub productServiceAsync;
    private final RecordServiceGrpc.RecordServiceBlockingStub recordService;

    @EventListener(ApplicationReadyEvent.class)
    public void runDemo() throws InterruptedException {
        Random random = new Random();
        int randomSuffix = random.nextInt(10000);
        String testEmail = "user" + randomSuffix + "@test.com";

        log.info("\n=== USER OPERATIONS ===");
        UserInput userInput = buildUserInput(testEmail);
        Id userId = userService.createUser(userInput);
        User user = userService.getUserById(userId);
        log.info("Created user: {}", user);

        log.info("\n=== BRAND OPERATIONS ===");
        BrandInput brandInput = buildBrandInput(randomSuffix);
        Id brandId = brandService.createBrand(brandInput);
        Brand brand = brandService.getBrandById(brandId);
        log.info("Created brand: {}", brand);

        log.info("\n=== PRODUCT OPERATIONS ===");
        ProductInput productInput = buildProductInput(randomSuffix, brandId.getId());
        Id productId = productService.createProduct(productInput);
        Product product = productService.getProductById(productId);
        log.info("Created product: {}", product);

        GetProductsResponse productsResponse = productService.getProducts(
                GetProductsRequest.newBuilder()
                        .setPagination(Pagination.newBuilder().setOffset(0).setLimit(10).build())
                        .build());
        log.info("Products meta: {}", productsResponse.getMeta());
        log.info("Found {} products", productsResponse.getProductsList().size());

        ProductInput updatedProductInput = productInput.toBuilder()
                .setName("Updated " + productInput.getName())
                .setCalories(250)
                .build();
        Product updatedProduct = productService.updateProduct(
                UpdateProductRequest.newBuilder()
                        .setId(productId.getId())
                        .setInput(updatedProductInput)
                        .build());
        log.info("Updated product: {}", updatedProduct.getName());

        log.info("\n=== RECORD OPERATIONS ===");
        RecordInput recordInput = buildRecordInput(productId.getId());
        Id recordId = recordService.createRecord(
                CreateRecordRequest.newBuilder()
                        .setUserId(userId.getId())
                        .setInput(recordInput)
                        .build());
        log.info("Created record: {}", recordId);

        Record record = recordService.getRecord(
                RecordRequest.newBuilder()
                        .setUserId(userId.getId())
                        .setId(recordId.getId())
                        .build());
        log.info("😘😘😘😘😘Products in record: {}, size {}", record.getProductsList(), record.getProductsList().size());

        GetRecordsResponse recordsResponse = recordService.getRecords(
                GetRecordsRequest.newBuilder()
                        .setUserId(userId.getId())
                        .setPagination(Pagination.newBuilder().setOffset(0).setLimit(10).build())
                        .build());
        log.info("Records meta: {}", recordsResponse.getMeta());
        log.info("Found {} records", recordsResponse.getRecordsList().size());

        RecordInput updatedRecordInput = recordInput.toBuilder()
                .setMealType(MealType.DINNER)
                .build();
        Record updatedRecord = recordService.updateRecord(
                UpdateRecordRequest.newBuilder()
                        .setRecordRequest(RecordRequest.newBuilder()
                                .setUserId(userId.getId())
                                .setId(recordId.getId())
                                .build())
                        .setInput(updatedRecordInput)
                        .build());
        log.info("Updated record meal type to: {}", updatedRecord.getMealType());

        log.info("\n=== STREAMING OPERATIONS ===");
        demoStreamingOperations(brandId.getId());

        log.info("\n=== CLEANUP ===");
        cleanupResources(userId, brandId, productId, recordId);
    }

    private UserInput buildUserInput(String email) {
        return UserInput.newBuilder()
                .setEmail(email)
                .setFirstName("Test")
                .setLastName("User")
                .setTelephone("+1234567890")
                .setBirthDate(Date.newBuilder().setYear(1990).setMonth(5).setDay(15))
                .setGender(Gender.MALE)
                .setWeight(75.0)
                .setHeight(180.0)
                .setActivityLevel(ActivityLevel.MODERATE)
                .setGoal(Goal.MAINTAIN)
                .setTargetWeight(75.0)
                .build();
    }

    private BrandInput buildBrandInput(int randomSuffix) {
        return BrandInput.newBuilder()
                .setName("TestBrand " + randomSuffix)
                .setDescription("Test brand description")
                .build();
    }

    private ProductInput buildProductInput(int randomSuffix, String brandId) {
        return ProductInput.newBuilder()
                .setName("TestProduct " + randomSuffix)
                .setBarcode("12345" + randomSuffix)
                .setProteins(10.0)
                .setFats(5.0)
                .setCarbohydrates(20.0)
                .setWater(60.0)
                .setSalt(1.0)
                .setSugar(12.0)
                .setFiber(3.0)
                .setAlcohol(0.0)
                .setDescription("Test product description")
                .setCalories(200)
                .setMeasurementUnit(MeasurementUnit.GRAMS)
                .setBrandId(brandId)
                .build();
    }

    private RecordInput buildRecordInput(String productId) {
        return RecordInput.newBuilder()
                .addProducts(ProductRecord.newBuilder()
                        .setProductId(productId)
                        .setQuantity(100.0)
                        .build())
                .setMealType(MealType.LUNCH)
                .build();
    }

    private void demoStreamingOperations(String brandId) throws InterruptedException {
        Random random = new Random();
        CountDownLatch serverStreamLatch = new CountDownLatch(1);
        productServiceAsync.streamProducts(
                StreamProductsRequest.newBuilder()
                        .setMaxCalories(1000)
                        .setBatchSize(2)
                        .build(),
                new StreamObserver<>() {
                    @Override
                    public void onNext(Product product) {
                        log.info("Streamed product: {}", product.getName());
                    }
                    @Override
                    public void onError(Throwable t) {
                        log.error("Stream error", t);
                        serverStreamLatch.countDown();
                    }
                    @Override
                    public void onCompleted() {
                        log.info("Server streaming completed");
                        serverStreamLatch.countDown();
                    }
                });

        Thread.sleep(10000);
        CountDownLatch biDiLatch = new CountDownLatch(1);
        StreamObserver<CreateProductStreamRequest> requestObserver =
                productServiceAsync.createProducts(new StreamObserver<>() {
                    @Override
                    public void onNext(CreateProductStreamResponse response) {
                        log.info("Server response: {}", response.getMessage());
                    }
                    @Override
                    public void onError(Throwable t) {
                        log.error("Bidirectional error", t);
                        biDiLatch.countDown();
                    }
                    @Override
                    public void onCompleted() {
                        log.info("Bidirectional completed");
                        biDiLatch.countDown();
                    }
                });

        for (int i = 1; i <= 3; i++) {
            ProductInput input = ProductInput.newBuilder()
                    .setName("StreamProduct" + i)
                    .setBarcode("STREAM" + random.nextInt() )
                    .setProteins(5.0 * i)
                    .setFats(2.0 * i)
                    .setCarbohydrates(15.0 * i)
                    .setWater(50.0)
                    .setSalt(0.5 * i)
                    .setSugar(5.0 * i)
                    .setFiber(2.0 * i)
                    .setAlcohol(0.0)
                    .setDescription("Stream product #" + i)
                    .setCalories(100 * i)
                    .setMeasurementUnit(MeasurementUnit.GRAMS)
                    .setBrandId(brandId)
                    .build();

            requestObserver.onNext(CreateProductStreamRequest.newBuilder()
                    .setRequestId(UUID.randomUUID().toString())
                    .setClientId("demo-client")
                    .setProduct(input)
                    .build());
        }
        requestObserver.onCompleted();

        serverStreamLatch.await(10, TimeUnit.SECONDS);
        biDiLatch.await(10, TimeUnit.SECONDS);
    }

    private void cleanupResources(Id userId, Id brandId, Id productId, Id recordId) {
        BooleanResponse deletedRecord = recordService.deleteRecord(
                RecordRequest.newBuilder()
                        .setUserId(userId.getId())
                        .setId(recordId.getId())
                        .build());
        log.info("Deleted record? {}", deletedRecord.getSuccess());

        BooleanResponse deletedProduct = productService.deleteProduct(productId);
        log.info("Deleted product? {}", deletedProduct.getSuccess());

        BooleanResponse deletedBrand = brandService.deleteBrand(brandId);
        log.info("Deleted brand? {}", deletedBrand.getSuccess());

        BooleanResponse deletedUser = userService.deleteUser(userId);
        log.info("Deleted user? {}", deletedUser.getSuccess());
    }
}