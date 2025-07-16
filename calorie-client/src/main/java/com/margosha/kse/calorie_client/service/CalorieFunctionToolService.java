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
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CalorieFunctionToolService {

    private final ProductClient productClient;
    private final UserClient userClient;
    private final RecordClient recordClient;

    public PaginatedResponse<Product> execute(SearchProducts searchProducts) {
        try {
            return productClient.getProducts(buildPagination(searchProducts.getLimit(), searchProducts.getPage()),
                    searchProducts.getName()).block();
        } catch (Exception e) {
            log.error("Error searching products: {}", e.getMessage());
            throw new RuntimeException("Failed to search products: " + e.getMessage());
        }
    }

    public IdResult execute(CreateUser createUser) {
        try {
            UUID userId = userClient.createUser(mapToUser(createUser)).block();
            return new IdResult(userId.toString(), "User created successfully");
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

    public IdResult execute(CreateFoodRecord createRecord) {
        try {
            RecordRequest recordRequest = buildRecordRequest(createRecord.getMealType(), createRecord.getProducts());
            UUID recordId = recordClient.createRecord(UUID.fromString(createRecord.getUserId()), recordRequest).block();
            return new IdResult(recordId.toString(), "Food record created successfully");
        } catch (Exception e) {
            log.error("Error creating food record: {}", e.getMessage());
            throw new RuntimeException("Failed to create food record: " + e.getMessage());
        }
    }

    public PaginatedResponse<RecordResponse> execute(GetUserRecords getUserRecords) {
        try {
            return recordClient.getRecords(buildPagination(getUserRecords.getLimit(), getUserRecords.getPage()),
                    UUID.fromString(getUserRecords.getUserId()), resolveDate(getUserRecords.getDate())).block();
        } catch (Exception e) {
            log.error("Error getting user records: {}", e.getMessage());
            throw new RuntimeException("Failed to get user records: " + e.getMessage());
        }
    }

    public RecordResponse execute(GetSingleRecord getSingleRecord) {
        try {
            return recordClient.getRecord(
                    UUID.fromString(getSingleRecord.getUserId()),
                    UUID.fromString(getSingleRecord.getRecordId())
            ).block();
        } catch (Exception e) {
            log.error("Error getting single record: {}", e.getMessage());
            throw new RuntimeException("Failed to get record: " + e.getMessage());
        }
    }

    public IdResult execute(UpdateFoodRecord updateRecord) {
        try {
            RecordRequest recordRequest = buildRecordRequest(updateRecord.getMealType(), updateRecord.getProducts());
            recordClient.updateRecord(
                    UUID.fromString(updateRecord.getUserId()),
                    UUID.fromString(updateRecord.getRecordId()),
                    recordRequest
            ).block();
            return new IdResult(updateRecord.getRecordId(), "Food record updated successfully");
        } catch (Exception e) {
            log.error("Error updating food record: {}", e.getMessage());
            throw new RuntimeException("Failed to update food record: " + e.getMessage());
        }
    }

    public IdResult execute(DeleteFoodRecord deleteRecord) {
        try {
            recordClient.deleteRecord(
                    UUID.fromString(deleteRecord.getUserId()),
                    UUID.fromString(deleteRecord.getRecordId())
            ).block();
            return new IdResult(deleteRecord.getRecordId(), "Food record deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting food record: {}", e.getMessage());
            throw new RuntimeException("Failed to delete food record: " + e.getMessage());
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
            case "GetSingleRecord" -> execute((GetSingleRecord) functionInstance);
            case "UpdateFoodRecord" -> execute((UpdateFoodRecord) functionInstance);
            case "DeleteFoodRecord" -> execute((DeleteFoodRecord) functionInstance);
            default -> throw new IllegalArgumentException("Unknown function: " + functionName);
        };
    }

    private RecordRequest buildRecordRequest(String mealType, Set<ProductRecord> products) {
        RecordRequest request = new RecordRequest();
        request.setMealType(MealType.valueOf(mealType.toUpperCase()));
        Set<ProductRecordInRequest> productRecords = products.stream().map(p -> {
            ProductRecordInRequest record = new ProductRecordInRequest();
            record.setProductId(UUID.fromString(p.getProductId()));
            record.setQuantity(p.getQuantity());
            return record;
        }).collect(Collectors.toSet());
        request.setProductRecords(productRecords);
        return request;
    }

    private LocalDate resolveDate(String dateStr) {
        if (dateStr == null) return null;
        return switch (dateStr.toLowerCase()) {
            case "today" -> LocalDate.now();
            case "yesterday" -> LocalDate.now().minusDays(1);
            default -> LocalDate.parse(dateStr);
        };
    }

    private Pagination buildPagination(Integer limit, Integer page) {
        Pagination pagination = new Pagination();
        pagination.setLimit(limit != null ? limit : 20);
        pagination.setOffset(page != null ? page : 1);
        return pagination;
    }

    private User mapToUser(CreateUser dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setBirthDate(LocalDate.parse(dto.getBirthDate()));
        user.setGender(Gender.valueOf(dto.getGender().toUpperCase()));
        user.setWeight(dto.getWeight());
        user.setHeight(dto.getHeight());
        user.setActivityLevel(ActivityLevel.valueOf(dto.getActivityLevel().toUpperCase()));
        user.setGoal(Goal.valueOf(dto.getGoal().toUpperCase()));
        user.setTargetWeight(dto.getTargetWeight());
        return user;
    }
}
