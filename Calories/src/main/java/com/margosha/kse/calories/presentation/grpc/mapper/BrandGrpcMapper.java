package com.margosha.kse.calories.presentation.grpc.mapper;

import com.margosha.kse.calories.business.dto.BrandDto;
import com.margosha.kse.calories.proto.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {CommonGrpcMapper.class},
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface BrandGrpcMapper {

    @Mapping(target = "id", qualifiedByName = "uuidToString")
    Brand toProto(BrandDto brandDto);

    @Mapping(target = "id", ignore = true)
    BrandDto toDto(BrandInput brandInput);

    @Named("nullToDefaultBrand")
    default Brand nullToDefaultBrand(BrandDto brandDto) {
        if (brandDto == null) {
            return Brand.newBuilder().setId("").setName("No brand:::::(((((((").build();
        }
        return toProto(brandDto);
    }
}