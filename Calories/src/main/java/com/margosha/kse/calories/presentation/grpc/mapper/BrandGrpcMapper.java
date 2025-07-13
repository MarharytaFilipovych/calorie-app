package com.margosha.kse.calories.presentation.grpc.mapper;

import com.margosha.kse.calories.business.dto.BrandDto;
import com.margosha.kse.calories.proto.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {CommonGrpcMapper.class})
public interface BrandGrpcMapper {

    @Mapping(target = "id", qualifiedByName = "uuidToString")
    @Mapping(target = "name", qualifiedByName = "nullToEmptyString")
    @Mapping(target = "description", qualifiedByName = "nullToEmptyString")
    Brand toProto(BrandDto brandDto);

    @Mapping(target = "id", ignore = true)
    BrandDto fromProtoInput(BrandInput brandInput);

    default Brand toProtoBrand(BrandDto brandDto) {
        if (brandDto == null) return Brand.getDefaultInstance();
        return toProto(brandDto);
    }
}