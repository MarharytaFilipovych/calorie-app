package com.margosha.kse.calories.presentation.grpc.services;

import com.margosha.kse.calories.business.dto.RecordRequestDto;
import com.margosha.kse.calories.business.dto.RecordResponseDto;
import com.margosha.kse.calories.business.service.RecordService;
import com.margosha.kse.calories.presentation.grpc.mapper.CommonGrpcMapper;
import com.margosha.kse.calories.presentation.grpc.mapper.RecordGrpcMapper;
import com.margosha.kse.calories.proto.*;
import com.margosha.kse.calories.proto.common.BooleanResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import java.time.LocalDate;
import java.util.UUID;

@GrpcService
public class RecordGrpcService extends RecordServiceGrpc.RecordServiceImplBase {
    private final RecordService recordService;
    private final RecordGrpcMapper recordMapper;
    private final CommonGrpcMapper commonMapper;

    public RecordGrpcService(RecordService recordService, RecordGrpcMapper recordMapper, CommonGrpcMapper commonMapper) {
        this.recordService = recordService;
        this.recordMapper = recordMapper;
        this.commonMapper = commonMapper;
    }

    @Override
    public void getRecords(GetRecordsRequest request, StreamObserver<GetRecordsResponse> responseObserver) {
        UUID userId = commonMapper.stringToUuid(request.getUserId());
        int limit = request.getPagination().getLimit() > 0 ? request.getPagination().getLimit() : 20;
        int offset = request.getPagination().getOffset() > 0 ? request.getPagination().getOffset() : 1;
        LocalDate date = request.hasDate() ? commonMapper.protoDateToLocalDate(request.getDate()) : null;

        Page<RecordResponseDto> records = recordService.getRecords(userId, limit, offset, date, true);
        
        GetRecordsResponse response = GetRecordsResponse.newBuilder()
                .addAllRecords(recordMapper.toProtoRecords(records.getContent()))
                .setMeta(commonMapper.toProto(records))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getRecord(GetRecordRequest request, StreamObserver<com.margosha.kse.calories.proto.Record> responseObserver) {
        UUID userId = commonMapper.stringToUuid(request.getUserId());
        UUID recordId = commonMapper.stringToUuid(request.getId());
        
        RecordResponseDto recordDto = recordService.getConsumption(userId, recordId);
        
        responseObserver.onNext(recordMapper.toProto(recordDto));
        responseObserver.onCompleted();
    }

    @Override
    public void createRecord(CreateRecordRequest request, StreamObserver<CreateRecordResponse> responseObserver) {
        UUID userId = commonMapper.stringToUuid(request.getUserId());
        RecordRequestDto recordDto = recordMapper.fromProtoInput(request.getInput());
        RecordResponseDto created = recordService.createRecord(userId, recordDto);
        
        CreateRecordResponse response = CreateRecordResponse.newBuilder()
                .setId(commonMapper.uuidToString(created.getId()))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateRecord(UpdateRecordRequest request, StreamObserver<com.margosha.kse.calories.proto.Record> responseObserver) {
        UUID userId = commonMapper.stringToUuid(request.getUserId());
        UUID recordId = commonMapper.stringToUuid(request.getId());
        RecordRequestDto recordDto = recordMapper.fromProtoInput(request.getInput());
        
        RecordResponseDto updated = recordService.updateRecord(userId, recordId, recordDto);
        
        responseObserver.onNext(recordMapper.toProto(updated));
        responseObserver.onCompleted();
    }

    @Override
    public void deleteRecord(DeleteRecordRequest request, StreamObserver<BooleanResponse> responseObserver) {
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