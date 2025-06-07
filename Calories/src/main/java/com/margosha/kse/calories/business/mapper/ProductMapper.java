package com.margosha.kse.calories.business.mapper;

import com.margosha.kse.calories.data.entity.Product;
import com.margosha.kse.calories.business.dto.ProductDto;
import com.margosha.kse.calories.presentation.enums.MeasurementUnit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "archived", target = "archived")
    ProductDto toDto(Product product);

    @Mapping(target = "calories", expression = "java(calculateCalories(productDto))")
    Product toEntity(ProductDto productDto);

    // Cloud ai generated
    default int calculateCalories(ProductDto dto){
        if (dto.getCalories() != null) return dto.getCalories();
        double calories = (dto.getProteins() * 4) +
                (dto.getFats() * 9) +
                (dto.getCarbohydrates() * 4) +
                (dto.getAlcohol() * 7);
        return (int) Math.round(calories);
    }

    default MeasurementUnit map(com.margosha.kse.calories.data.enums.MeasurementUnit measurementUnit) {
        return MeasurementUnit.valueOf(measurementUnit.name());
    }

    default com.margosha.kse.calories.data.enums.MeasurementUnit map(MeasurementUnit measurementUnit) {
        return com.margosha.kse.calories.data.enums.MeasurementUnit.valueOf(measurementUnit.name());
    }
}
