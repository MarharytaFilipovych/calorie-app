package com.margosha.kse.calorie_client.service;

import com.margosha.kse.calorie_client.clients.ProductClient;
import com.margosha.kse.calorie_client.clients.RecordClient;
import com.margosha.kse.calorie_client.clients.UserClient;
import com.margosha.kse.calorie_client.dto.*;
import com.margosha.kse.calorie_client.dto.subdto.ProductRecordInRequest;
import com.margosha.kse.calorie_client.enums.ActivityLevel;
import com.margosha.kse.calorie_client.enums.Gender;
import com.margosha.kse.calorie_client.enums.Goal;
import com.margosha.kse.calorie_client.enums.MealType;
import com.margosha.kse.calorie_client.model.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class CalorieFunctionToolService {

    private final ProductClient productClient;
    private final UserClient userClient;
    private final RecordClient recordClient;

    public PaginatedResponse<Product> execute(SearchProducts searchProducts) {
        try {
            Pagination pagination = new Pagination();
            pagination.setLimit(searchProducts.getLimit() != null ? searchProducts.getLimit() : 20);
            pagination.setOffset(searchProducts.getPage() != null ? searchProducts.getPage() : 1);

            return productClient.getProducts(pagination, searchProducts.getName()).block();
        } catch (Exception e) {
            log.error("Error searching products: {}", e.getMessage());
            throw new RuntimeException("Failed to search products: " + e.getMessage());
        }
    }

    public CreateUserResult execute(CreateUser createUser) {
        try {
            User user = new User();
            user.setEmail(createUser.getEmail());
            user.setFirstName(createUser.getFirstName());
            user.setLastName(createUser.getLastName());
            user.setBirthDate(LocalDate.parse(createUser.getBirthDate()));
            user.setGender(Gender.valueOf(createUser.getGender().toUpperCase()));
            user.setWeight(createUser.getWeight());
            user.setHeight(createUser.getHeight());
            user.setActivityLevel(ActivityLevel.valueOf(createUser.getActivityLevel().toUpperCase()));
            user.setGoal(Goal.valueOf(createUser.getGoal().toUpperCase()));
            user.setTargetWeight(createUser.getTargetWeight());
            user.setTelephone(createUser.getTelephone());

            UUID userId = userClient.createUser(user).block();
            return new CreateUserResult(userId.toString(), "User created successfully");
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage());
            throw new RuntimeException("Failed to create user: " + e.getMessage());
        }
    }

    public User execute(GetUser getUser) {
        try {
            return userClient.getUser(UUID.fromString(getUser.getUserId())).block();
        } catch (Exception e) {
            log.error("Error getting user: {}", e.getMessage());
            throw new RuntimeException("Failed to get user: " + e.getMessage());
        }
    }

    public CreateRecordResult execute(CreateFoodRecord createFoodRecord) {
        try {
            RecordRequest recordRequest = new RecordRequest();
            recordRequest.setMealType(MealType.valueOf(createFoodRecord.getMealType().toUpperCase()));

            Set<ProductRecordInRequest> productRecords = new HashSet<>();
            for (ProductRecord productRecord : createFoodRecord.getProducts()) {
                productRecords.add(new ProductRecordInRequest(
                        UUID.fromString(productRecord.getProductId()),
                        productRecord.getQuantity()
                ));
            }
            recordRequest.setProductRecords(productRecords);

            UUID recordId = recordClient.createRecord(UUID.fromString(createFoodRecord.getUserId()), recordRequest).block();
            return new CreateRecordResult(recordId.toString(), "Food record created successfully");
        } catch (Exception e) {
            log.error("Error creating food record: {}", e.getMessage());
            throw new RuntimeException("Failed to create food record: " + e.getMessage());
        }
    }

    public PaginatedResponse<RecordResponse> execute(GetUserRecords getUserRecords) {
        try {
            Pagination pagination = new Pagination();
            pagination.setLimit(getUserRecords.getLimit() != null ? getUserRecords.getLimit() : 20);
            pagination.setOffset(getUserRecords.getPage() != null ? getUserRecords.getPage() : 1);

            LocalDate filterDate = getUserRecords.getDate() != null ? LocalDate.parse(getUserRecords.getDate()) : null;

            return recordClient.getRecords(pagination, UUID.fromString(getUserRecords.getUserId()), filterDate).block();
        } catch (Exception e) {
            log.error("Error getting user records: {}", e.getMessage());
            throw new RuntimeException("Failed to get user records: " + e.getMessage());
        }
    }

    public Object executeFunction(String functionName, Object functionInstance) {
        log.info("Executing function: {}", functionName);
        return switch (functionName) {
            case "SearchProducts" -> execute((SearchProducts) functionInstance);
            case "CreateUser" -> execute((CreateUser) functionInstance);
            case "GetUser" -> execute((GetUser) functionInstance);
            case "CreateFoodRecord" -> execute((CreateFoodRecord) functionInstance);
            case "GetUserRecords" -> execute((GetUserRecords) functionInstance);
            default -> throw new IllegalArgumentException("Unknown function: " + functionName);
        };
    }
}

