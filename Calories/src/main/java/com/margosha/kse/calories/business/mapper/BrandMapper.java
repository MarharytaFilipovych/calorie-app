package com.margosha.kse.calories.business.mapper;

import com.margosha.kse.calories.business.dto.BrandDto;
import com.margosha.kse.calories.data.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface BrandMapper {
    BrandDto toDto(Brand brand);
    Brand toEntity(BrandDto dto);
}
