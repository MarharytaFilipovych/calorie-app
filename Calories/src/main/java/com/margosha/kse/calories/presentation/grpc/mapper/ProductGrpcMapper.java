package com.margosha.kse.calories.presentation.grpc.mapper;

import com.margosha.kse.calories.business.dto.*;
import com.margosha.kse.calories.business.dto.subdto.ProductRecordInRequestDto;
import com.margosha.kse.calories.business.dto.subdto.ProductRecordInResponseDto;
import com.margosha.kse.calories.proto.*;
import com.margosha.kse.calories.presentation.enums.MeasurementUnit;
import org.mapstruct.*;

import java.util.*;

@Mapper(componentModel = "spring", uses = {CommonGrpcMapper.class, BrandGrpcMapper.class})
public interface ProductGrpcMapper {

    @Mapping(target = "id", qualifiedByName = "uuidToString")
    @Mapping(target = "name", qualifiedByName = "nullToEmptyString")
    @Mapping(target = "barcode", qualifiedByName = "nullToEmptyString")
    @Mapping(target = "proteins", qualifiedByName = "nullToZeroDouble")
    @Mapping(target = "fats", qualifiedByName = "nullToZeroDouble")
    @Mapping(target = "carbohydrates", qualifiedByName = "nullToZeroDouble")
    @Mapping(target = "water", qualifiedByName = "nullToZeroDouble")
    @Mapping(target = "salt", qualifiedByName = "nullToZeroDouble")
    @Mapping(target = "sugar", qualifiedByName = "nullToZeroDouble")
    @Mapping(target = "fiber", qualifiedByName = "nullToZeroDouble")
    @Mapping(target = "alcohol", qualifiedByName = "nullToZeroDouble")
    @Mapping(target = "description", qualifiedByName = "nullToEmptyString")
    @Mapping(target = "calories", qualifiedByName = "nullToZeroInt")
    Product toProto(ProductResponseDto productDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "brandId", source = "brandId", qualifiedByName = "stringToUuid")
    ProductRequestDto fromProtoInput(ProductInput productInput);

    ProductRecordResponse toProtoProductRecord(ProductRecordInResponseDto productRecord);

    @Mapping(target = "productId", source = "productId", qualifiedByName = "stringToUuid")
    ProductRecordInRequestDto fromProtoProductRecord(ProductRecord productRecord);


    default List<ProductRecordResponse> toProtoProductRecords(Set<ProductRecordInResponseDto> productRecords) {
        if (productRecords == null || productRecords.isEmpty()) return new ArrayList<>();
        return productRecords.stream()
                .map(this::toProtoProductRecord)
                .toList();
    }

    default Set<ProductRecordInRequestDto> fromProtoProductRecords(List<ProductRecord> productRecords) {
        if (productRecords == null || productRecords.isEmpty()) return new HashSet<>();
        return productRecords.stream()
                .map(this::fromProtoProductRecord)
                .collect(HashSet::new, HashSet::add, HashSet::addAll);
    }

    default com.margosha.kse.calories.proto.MeasurementUnit measurementUnitToProto(MeasurementUnit unit) {
        if (unit == null) return com.margosha.kse.calories.proto.MeasurementUnit.GRAMS;
        return switch (unit) {
            case GRAMS -> com.margosha.kse.calories.proto.MeasurementUnit.GRAMS;
            case MILLILITERS -> com.margosha.kse.calories.proto.MeasurementUnit.MILLILITERS;
            case PIECES -> com.margosha.kse.calories.proto.MeasurementUnit.PIECES;
        };
    }

    default MeasurementUnit protoToMeasurementUnit(com.margosha.kse.calories.proto.MeasurementUnit unit) {
        if (unit == null) return MeasurementUnit.GRAMS;
        return switch (unit) {
            case GRAMS -> MeasurementUnit.GRAMS;
            case MILLILITERS -> MeasurementUnit.MILLILITERS;
            case PIECES -> MeasurementUnit.PIECES;
            case UNRECOGNIZED -> throw new IllegalArgumentException("Unknown measurement unit: " + unit);
        };
    }
}