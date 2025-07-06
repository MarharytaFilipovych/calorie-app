package com.margosha.kse.calories.business.mapper;

import com.margosha.kse.calories.business.dto.BrandDto;
import com.margosha.kse.calories.data.entity.Brand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    BrandDto toDto(Brand brand);
    Brand toEntity(BrandDto dto);
}
