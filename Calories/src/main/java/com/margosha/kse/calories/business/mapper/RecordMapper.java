package com.margosha.kse.calories.business.mapper;

import com.margosha.kse.calories.business.dto.subdto.ProductRecordInResponseDto;
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
        return dto;
    }
}
