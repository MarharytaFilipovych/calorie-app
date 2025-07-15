package com.margosha.kse.calories.presentation.grpc.services;

import com.margosha.kse.calories.business.dto.BrandDto;
import com.margosha.kse.calories.business.service.BrandService;
import com.margosha.kse.calories.presentation.annotations.OptionalGrpcRequest;
import com.margosha.kse.calories.presentation.grpc.mapper.BrandGrpcMapper;
import com.margosha.kse.calories.presentation.grpc.mapper.CommonGrpcMapper;
import com.margosha.kse.calories.presentation.model.Pagination;
import com.margosha.kse.calories.proto.*;
import com.margosha.kse.calories.proto.common.BooleanResponse;
import com.margosha.kse.calories.proto.common.Id;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import java.util.UUID;

@GrpcService
public class BrandGrpcService extends BrandServiceGrpc.BrandServiceImplBase {
    private final BrandService brandService;
    private final BrandGrpcMapper brandMapper;
    private final CommonGrpcMapper commonMapper;

    public BrandGrpcService(BrandService brandService, BrandGrpcMapper brandGrpcMapper, CommonGrpcMapper commonGrpcMapper) {
        this.brandService = brandService;
        this.brandMapper = brandGrpcMapper;
        this.commonMapper = commonGrpcMapper;
    }

    @Override
    @OptionalGrpcRequest
    public void getAllBrands(GetAllBrandsRequest request, StreamObserver<GetAllBrandsResponse> responseObserver) {
        Pagination pagination = commonMapper.toModel(request.getPagination());
        Page<BrandDto> brands = brandService.getAllBrands(pagination.getLimit(), pagination.getOffset());
        
        GetAllBrandsResponse response = GetAllBrandsResponse.newBuilder()
                .addAllBrands(brands.getContent().stream().map(brandMapper::toProto).toList())
                .setMeta(commonMapper.toProto(brands))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getBrandById(Id request, StreamObserver<Brand> responseObserver) {
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
    public void createBrand(BrandInput request, StreamObserver<Id> responseObserver) {
        BrandDto brandDto = brandMapper.toDto(request);
        BrandDto created = brandService.createBrand(brandDto);
        responseObserver.onNext(Id.newBuilder().setId(created.getId().toString()).build());
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
    public void deleteBrand(Id request, StreamObserver<BooleanResponse> responseObserver) {
        UUID id = commonMapper.stringToUuid(request.getId());
        boolean deleted = brandService.deleteBrand(id);
        
        BooleanResponse response = BooleanResponse.newBuilder()
                .setSuccess(deleted)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}