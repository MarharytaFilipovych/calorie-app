package com.margosha.kse.calories.presentation.grpc.services;

import com.margosha.kse.calories.business.dto.BrandDto;
import com.margosha.kse.calories.business.service.BrandService;
import com.margosha.kse.calories.presentation.grpc.mapper.BrandGrpcMapper;
import com.margosha.kse.calories.presentation.grpc.mapper.CommonGrpcMapper;
import com.margosha.kse.calories.proto.*;
import com.margosha.kse.calories.proto.common.BooleanResponse;
import com.margosha.kse.calories.proto.common.IdRequest;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;

import java.util.UUID;

@GrpcService
public class BrandGrpcService extends BrandServiceGrpc.BrandServiceImplBase {
    private final BrandService brandService;
    private final BrandGrpcMapper brandMapper;
    private final CommonGrpcMapper commonMapper;

    public BrandGrpcService(BrandService brandService, BrandGrpcMapper brandMapper, CommonGrpcMapper commonMapper) {
        this.brandService = brandService;
        this.brandMapper = brandMapper;
        this.commonMapper = commonMapper;
    }

    @Override
    public void getAllBrands(GetAllBrandsRequest request, StreamObserver<GetAllBrandsResponse> responseObserver) {
        int limit = request.getPagination().getLimit() > 0 ? request.getPagination().getLimit() : 20;
        int offset = request.getPagination().getOffset() > 0 ? request.getPagination().getOffset() : 1;

        Page<BrandDto> brands = brandService.getAllBrands(limit, offset);
        
        GetAllBrandsResponse response = GetAllBrandsResponse.newBuilder()
                .addAllBrands(brands.getContent().stream().map(brandMapper::toProto).toList())
                .setMeta(commonMapper.toProto(brands))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getBrandById(IdRequest request, StreamObserver<Brand> responseObserver) {
        UUID id = commonMapper.stringToUuid(request.getId());
        BrandDto brandDto = brandService.getBrandById(id);
        
        responseObserver.onNext(brandMapper.toProto(brandDto));
        responseObserver.onCompleted();
    }

    @Override
    public void getBrandByName(GetBrandByNameRequest request, StreamObserver<Brand> responseObserver) {
        BrandDto brandDto = brandService.getBrandByName(request.getName());
        
        responseObserver.onNext(brandMapper.toProto(brandDto));
        responseObserver.onCompleted();
    }

    @Override
    public void createBrand(CreateBrandRequest request, StreamObserver<Brand> responseObserver) {
        BrandDto brandDto = brandMapper.toDto(request.getInput());
        BrandDto created = brandService.createBrand(brandDto);
        
        responseObserver.onNext(brandMapper.toProto(created));
        responseObserver.onCompleted();
    }

    @Override
    public void updateBrand(UpdateBrandRequest request, StreamObserver<Brand> responseObserver) {
        UUID id = commonMapper.stringToUuid(request.getId());
        BrandDto brandDto = brandMapper.toDto(request.getInput());
        BrandDto updated = brandService.updateBrand(brandDto, id);
        
        responseObserver.onNext(brandMapper.toProto(updated));
        responseObserver.onCompleted();
    }

    @Override
    public void deleteBrand(IdRequest request, StreamObserver<BooleanResponse> responseObserver) {
        UUID id = commonMapper.stringToUuid(request.getId());
        boolean deleted = brandService.deleteBrand(id);
        
        BooleanResponse response = BooleanResponse.newBuilder()
                .setSuccess(deleted)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}