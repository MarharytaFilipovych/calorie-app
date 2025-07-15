package com.margosha.kse.calories.presentation.grpc.services;

import com.margosha.kse.calories.business.dto.RecordRequestDto;
import com.margosha.kse.calories.business.dto.RecordResponseDto;
import com.margosha.kse.calories.business.service.RecordService;
import com.margosha.kse.calories.presentation.annotations.OptionalGrpcRequest;
import com.margosha.kse.calories.presentation.grpc.mapper.CommonGrpcMapper;
import com.margosha.kse.calories.presentation.grpc.mapper.RecordGrpcMapper;
import com.margosha.kse.calories.presentation.model.Pagination;
import com.margosha.kse.calories.proto.*;
import com.margosha.kse.calories.proto.Record;
import com.margosha.kse.calories.proto.common.BooleanResponse;
import com.margosha.kse.calories.proto.common.Id;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@GrpcService
public class RecordGrpcService extends RecordServiceGrpc.RecordServiceImplBase {
    private final RecordService recordService;
    private final RecordGrpcMapper recordMapper;
    private final CommonGrpcMapper commonMapper;

    public RecordGrpcService(RecordService recordService, RecordGrpcMapper recordGrpcMapper, CommonGrpcMapper commonGrpcMapper) {
        this.recordService = recordService;
        this.recordMapper = recordGrpcMapper;
        this.commonMapper = commonGrpcMapper;
    }

    @Override
    @OptionalGrpcRequest
    public void getRecords(GetRecordsRequest request, StreamObserver<GetRecordsResponse> responseObserver) {
        UUID userId = commonMapper.stringToUuid(request.getUserId());
        Pagination pagination = commonMapper.toModel(request.getPagination());
        LocalDate date = request.hasDate() ? commonMapper.protoDateToLocalDate(request.getDate()) : null;
        Page<RecordResponseDto> records = recordService.getRecords(userId, pagination.getLimit(), pagination.getOffset(), date, true);
        
        GetRecordsResponse response = GetRecordsResponse.newBuilder()
                .addAllRecords(recordMapper.toProtoRecords(records.getContent()))
                .setMeta(commonMapper.toProto(records))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getRecord(RecordRequest request, StreamObserver<Record> responseObserver) {
       UUID userId = commonMapper.stringToUuid(request.getUserId());
        UUID recordId = commonMapper.stringToUuid(request.getId());
        RecordResponseDto recordDto = recordService.getConsumption(userId, recordId);
        responseObserver.onNext(recordMapper.toProto(recordDto));
        responseObserver.onCompleted();
    }

    @Override
    public void createRecord(CreateRecordRequest request, StreamObserver<Id> responseObserver) {
        UUID userId = commonMapper.stringToUuid(request.getUserId());
        RecordRequestDto recordDto = recordMapper.toDto(request.getInput());
        RecordResponseDto created = recordService.createRecord(userId, recordDto);
        responseObserver.onNext(Id.newBuilder().setId(created.getId().toString()).build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateRecord(UpdateRecordRequest request, StreamObserver<com.margosha.kse.calories.proto.Record> responseObserver) {
        UUID userId = commonMapper.stringToUuid(request.getRecordRequest().getUserId());
        UUID recordId = commonMapper.stringToUuid(request.getRecordRequest().getId());
        RecordRequestDto recordDto = recordMapper.toDto(request.getInput());
        RecordResponseDto updated = recordService.updateRecord(userId, recordId, recordDto);
        responseObserver.onNext(recordMapper.toProto(updated));
        responseObserver.onCompleted();
    }

    @Override
    public void deleteRecord(RecordRequest request, StreamObserver<BooleanResponse> responseObserver) {
        UUID userId = commonMapper.stringToUuid(request.getUserId());
        UUID recordId = commonMapper.stringToUuid(request.getId());
        boolean deleted = recordService.deleteRecord(userId, recordId);
        BooleanResponse response = BooleanResponse.newBuilder()
                .setSuccess(deleted)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}