package com.margosha.kse.calories.presentation.grpc.services;

import com.margosha.kse.calories.business.dto.UserDto;
import com.margosha.kse.calories.business.service.UserService;
import com.margosha.kse.calories.presentation.annotations.OptionalGrpcRequest;
import com.margosha.kse.calories.presentation.grpc.mapper.CommonGrpcMapper;
import com.margosha.kse.calories.presentation.grpc.mapper.UserGrpcMapper;
import com.margosha.kse.calories.presentation.model.Pagination;
import com.margosha.kse.calories.proto.*;
import com.margosha.kse.calories.proto.common.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import java.util.UUID;

@GrpcService
@Slf4j
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final UserService userService;
    private final UserGrpcMapper userMapper;
    private final CommonGrpcMapper commonMapper;

    public UserGrpcService(UserService userService,
                           UserGrpcMapper userGrpcMapper,
                           CommonGrpcMapper commonGrpcMapper) {
        this.userService = userService;
        this.userMapper = userGrpcMapper;
        this.commonMapper = commonGrpcMapper;
    }

    @Override
    @OptionalGrpcRequest
    public void getUsers(GetUsersRequest request, StreamObserver<GetUsersResponse> responseObserver) {
        try {
            Pagination pagination = commonMapper.toModel(request.getPagination());
            Page<UserDto> users = userService.getAllUsers(pagination.getLimit(), pagination.getOffset());

            GetUsersResponse response = GetUsersResponse.newBuilder()
                    .addAllUsers(users.getContent().stream().map(userMapper::toProto).toList())
                    .setMeta(commonMapper.toProto(users))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error in GetUsers", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to get users: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public void getUserById(Id request, StreamObserver<User> responseObserver) {
        UUID id = commonMapper.stringToUuid(request.getId());
        UserDto user = userService.getUserById(id);
        responseObserver.onNext(userMapper.toProto(user));
        responseObserver.onCompleted();
    }

    @Override
    public void getUserByEmail(GetUserByEmailRequest request, StreamObserver<User> responseObserver) {
        UserDto user = userService.getUserByEmail(request.getEmail());

        responseObserver.onNext(userMapper.toProto(user));
        responseObserver.onCompleted();
    }

    @Override
    public void createUser(UserInput request, StreamObserver<Id> responseObserver) {
        UserDto userDto = userMapper.toDto(request);
        UserDto created = userService.createUser(userDto);
        responseObserver.onNext(Id.newBuilder().setId(created.getId().toString()).build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateUser(UpdateUserRequest request, StreamObserver<User> responseObserver) {
        UUID id = commonMapper.stringToUuid(request.getId());
        UserDto userDto = userMapper.toDto(request.getInput());
        UserDto updated = userService.updateUser(userDto, id);
        responseObserver.onNext(userMapper.toProto(updated));
        responseObserver.onCompleted();
    }

    @Override
    public void deleteUser(Id request, StreamObserver<BooleanResponse> responseObserver) {
        UUID id = commonMapper.stringToUuid(request.getId());
        boolean deleted = userService.deleteUser(id);
        BooleanResponse response = BooleanResponse.newBuilder()
                .setSuccess(deleted)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getDailyTarget(Id request, StreamObserver<GetDailyTargetResponse> responseObserver) {
        UUID id = commonMapper.stringToUuid(request.getId());
        int dailyTarget = userService.getDailyTarget(id);

        GetDailyTargetResponse response = GetDailyTargetResponse.newBuilder()
                .setDailyCalorieTarget(dailyTarget)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}