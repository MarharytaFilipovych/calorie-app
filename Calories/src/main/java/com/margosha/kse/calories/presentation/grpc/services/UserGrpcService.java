package com.margosha.kse.calories.presentation.grpc.services;

import com.margosha.kse.calories.business.dto.UserDto;
import com.margosha.kse.calories.business.service.UserService;
import com.margosha.kse.calories.presentation.grpc.mapper.CommonGrpcMapper;
import com.margosha.kse.calories.presentation.grpc.mapper.UserGrpcMapper;
import com.margosha.kse.calories.proto.*;
import com.margosha.kse.calories.proto.common.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import java.util.UUID;

@GrpcService
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final UserService userService;
    private final UserGrpcMapper userMapper;
    private final CommonGrpcMapper commonMapper;

    public UserGrpcService(UserService userService,
                           UserGrpcMapper userMapper,
                           CommonGrpcMapper commonMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.commonMapper = commonMapper;
    }

    @Override
    public void getUsers(GetUsersRequest request, StreamObserver<GetUsersResponse> responseObserver) {
        Pagination pagination = request.getPagination();
        int limit = pagination.getLimit() > 0 ? pagination.getLimit() : 20;
        int offset = pagination.getOffset() > 0 ? pagination.getOffset() : 1;

        Page<UserDto> users = userService.getAllUsers(limit, offset);

        GetUsersResponse response = GetUsersResponse.newBuilder()
                .addAllUsers(users.getContent().stream().map(userMapper::toProto).toList())
                .setMeta(commonMapper.toProto(users))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getUserById(IdRequest request, StreamObserver<User> responseObserver) {
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
    public void createUser(CreateUserRequest request, StreamObserver<CreateUserResponse> responseObserver) {
        UserDto userDto = userMapper.fromProtoInput(request.getInput());
        UserDto created = userService.createUser(userDto);

        CreateUserResponse response = CreateUserResponse.newBuilder()
                .setId(commonMapper.uuidToString(created.getId()))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateUser(UpdateUserRequest request, StreamObserver<User> responseObserver) {
        UUID id = commonMapper.stringToUuid(request.getId());
        UserDto userDto = userMapper.fromProtoInput(request.getInput());
        UserDto updated = userService.updateUser(userDto, id);

        responseObserver.onNext(userMapper.toProto(updated));
        responseObserver.onCompleted();
    }

    @Override
    public void deleteUser(IdRequest request, StreamObserver<BooleanResponse> responseObserver) {
        UUID id = commonMapper.stringToUuid(request.getId());
        boolean deleted = userService.deleteUser(id);

        BooleanResponse response = BooleanResponse.newBuilder()
                .setSuccess(deleted)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getDailyTarget(GetDailyTargetRequest request, StreamObserver<GetDailyTargetResponse> responseObserver) {
        UUID id = commonMapper.stringToUuid(request.getId());
        int dailyTarget = userService.getDailyTarget(id);

        GetDailyTargetResponse response = GetDailyTargetResponse.newBuilder()
                .setDailyCalorieTarget(dailyTarget)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}