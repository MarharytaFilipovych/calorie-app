package com.margosha.kse.calories.business.service;

import com.margosha.kse.calories.business.dto.ProductDto;
import com.margosha.kse.calories.business.dto.RecordRequestDto;
import com.margosha.kse.calories.business.dto.RecordResponseDto;
import com.margosha.kse.calories.business.dto.subdto.ProductRecordInRequestDto;
import com.margosha.kse.calories.business.dto.subdto.ProductRecordInResponseDto;
import com.margosha.kse.calories.business.mapper.RecordMapper;
import com.margosha.kse.calories.data.entity.Product;
import com.margosha.kse.calories.data.entity.ProductRecord;
import com.margosha.kse.calories.data.entity.Record;
import com.margosha.kse.calories.data.entity.User;
import com.margosha.kse.calories.data.enums.MealType;
import com.margosha.kse.calories.data.repository.ProductRepository;
import com.margosha.kse.calories.data.repository.RecordRepository;
import com.margosha.kse.calories.data.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RecordService {
    private final UserRepository userRepository;
    private final RecordRepository recordRepository;
    private final ProductRepository productRepository;
    private final RecordMapper recordMapper;

    public RecordService(UserRepository userRepository, RecordRepository recordRepository, ProductRepository productRepository, RecordMapper recordMapper) {
        this.userRepository = userRepository;
        this.recordRepository = recordRepository;
        this.productRepository = productRepository;
        this.recordMapper = recordMapper;
    }

    public Page<RecordResponseDto> getRecords(UUID id, int limit, int offset, LocalDate date){
        if(!userRepository.existsById(id))throw new EntityNotFoundException(id.toString());
        Pageable pageable = PageRequest.of(offset - 1, limit);
        Page<UUID> uuidPage;
        if(date != null){
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(23, 59, 59);
            uuidPage = recordRepository.findIdsByUserIdAndDateTime(id, start, end, pageable);
        }
        else uuidPage = recordRepository.findIdsByUserId(id, pageable);
        if(!uuidPage.hasContent())return Page.empty();
        List<com.margosha.kse.calories.data.entity.Record> records = recordRepository.findAllByIdsWithProducts(uuidPage.getContent());
        List<RecordResponseDto> recordResponseDtos = records.stream()
                .map(recordMapper::toDto)
                .peek(this::calculateRecordTotals)
                .toList();
        return new PageImpl<>(recordResponseDtos, pageable, uuidPage.getTotalElements());
    }

    @Transactional
    public void updateRecord(UUID userId, UUID id, RecordRequestDto dto){
        com.margosha.kse.calories.data.entity.Record record = recordRepository.findByIdAndUser_Id(id, userId).orElseThrow(()-> new EntityNotFoundException(id.toString()));
        record.setMealType(MealType.valueOf(dto.getMealType().name()));
        Map<UUID, Product> existingProducts = getExistingProducts(dto);
        Set<ProductRecord> productRecords = record.getProductRecords();
        productRecords.clear();
        populateProductRecords(record, dto, existingProducts, productRecords);
        recordRepository.save(record);
    }

    @Transactional
    public void deleteRecord(UUID userId, UUID id){
        recordRepository.deleteByIdAndUser_Id(id, userId);
    }

    public RecordResponseDto getConsumption(UUID userId, UUID id){
        com.margosha.kse.calories.data.entity.Record record = recordRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new EntityNotFoundException(id.toString()));
        RecordResponseDto dto = recordMapper.toDto(record);
        calculateRecordTotals(dto);
        return dto;
    }

    @Transactional
    public UUID createRecord(UUID userId, RecordRequestDto dto){
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(userId.toString()));
        Map<UUID, Product> existingProducts = getExistingProducts(dto);
        com.margosha.kse.calories.data.entity.Record record = new com.margosha.kse.calories.data.entity.Record();
        record.setMealType(MealType.valueOf(dto.getMealType().name()));
        record.setUser(user);
        Set<ProductRecord> productRecords = getProductRecords(record, dto, existingProducts);
        record.setProductRecords(productRecords);
        com.margosha.kse.calories.data.entity.Record savedRecord = recordRepository.save(record);
        return savedRecord.getId();
    }

    private void validateProductsExist(Set<UUID> requestedIds, Map<UUID, Product> existingProducts) {
        List<UUID> missing = requestedIds.stream()
                .filter(id -> !existingProducts.containsKey(id))
                .toList();
        if (!missing.isEmpty()) {
            String joinedIds = missing.stream()
                    .map(UUID::toString)
                    .collect(Collectors.joining(", "));
            throw new EntityNotFoundException("Some of passed product ids were not found: " + joinedIds);
        }
    }

    private Map<UUID, Product> getExistingProducts(RecordRequestDto dto){
        Set<UUID> requestedProductIds = dto.getProductRecords().stream().map(ProductRecordInRequestDto::getProductId).collect(Collectors.toSet());
        Map<UUID, Product> existingProducts = productRepository.findAllByIdInAndArchivedIsFalse(requestedProductIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
        validateProductsExist(requestedProductIds, existingProducts);
        return existingProducts;
    }

    private Set<ProductRecord> getProductRecords(com.margosha.kse.calories.data.entity.Record record, RecordRequestDto dto, Map<UUID, Product> existingProducts){
        Set<ProductRecord> productRecords = new HashSet<>();
        populateProductRecords(record, dto, existingProducts, productRecords);
        return productRecords;
    }

    private void populateProductRecords(Record record, RecordRequestDto dto, Map<UUID, Product> existingProducts, Set<ProductRecord> productRecords){
        dto.getProductRecords().forEach(pr -> {
            ProductRecord productRecord = new ProductRecord();
            productRecord.setProduct(existingProducts.get(pr.getProductId()));
            productRecord.setQuantity(pr.getQuantity());
            productRecord.setRecord(record);
            productRecords.add(productRecord);
        });
    }

    // Cloud ai generated
    private void calculateRecordTotals(RecordResponseDto dto){
        int totalCalories = 0;
        double totalProteins = 0;
        double totalFats = 0;
        double totalCarbs = 0;
        double totalQuantity = 0;

        for (ProductRecordInResponseDto productRecord : dto.getProducts()) {
            double quantity = productRecord.getQuantity();
            double multiplier = quantity / 100.0;
            ProductDto product = productRecord.getProduct();
            totalCalories += (int) Math.round(product.getCalories() * multiplier);
            totalProteins += product.getProteins() * multiplier;
            totalFats += product.getFats() * multiplier;
            totalCarbs += product.getCarbohydrates() * multiplier;
            totalQuantity += quantity;
        }
        dto.setCaloriesConsumed(totalCalories);
        dto.setTotalProteins(Math.round(totalProteins * 100.0) / 100.0);
        dto.setTotalFats(Math.round(totalFats * 100.0) / 100.0);
        dto.setTotalCarbohydrates(Math.round(totalCarbs * 100.0) / 100.0);
        dto.setTotalQuantity(totalQuantity);
    }
}
