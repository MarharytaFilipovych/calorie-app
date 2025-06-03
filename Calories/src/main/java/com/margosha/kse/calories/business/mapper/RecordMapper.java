package com.margosha.kse.calories.business.mapper;

import com.margosha.kse.calories.business.dto.ProductDto;
import com.margosha.kse.calories.business.dto.RecordRequestDto;
import com.margosha.kse.calories.business.dto.subdto.ProductRecordInResponseDto;
import com.margosha.kse.calories.data.enums.MealType;
import com.margosha.kse.calories.data.entity.Record;
import com.margosha.kse.calories.business.dto.RecordResponseDto;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RecordMapper {
    private final ProductMapper productMapper;

    public RecordMapper(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public RecordResponseDto toDto(Record record) {
        RecordResponseDto dto = new RecordResponseDto();
        dto.setId(record.getId());
        dto.setMealType(com.margosha.kse.calories.presentation.enums.MealType.valueOf(dto.getMealType().name()));
        dto.setConsumedAt(record.getConsumedAt());
        Set<ProductRecordInResponseDto> products = record.getProductRecords().stream()
                .map(p -> new ProductRecordInResponseDto(
                        productMapper.toDto(p.getProduct()),
                        p.getQuantity()
                )).collect(Collectors.toSet());
        dto.setProducts(products);
        calculateRecordTotals(dto);
        return dto;
    }

    public Record toEntity(RecordRequestDto dto){
        Record record = new Record();
        record.setMealType(MealType.valueOf(dto.getMealType().name()));
        return record;
    }

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
