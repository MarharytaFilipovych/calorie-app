package com.margosha.kse.calories.presentation.grpc.mapper;

import com.margosha.kse.calories.presentation.grpc.exception.EmptyIdException;
import com.margosha.kse.calories.proto.common.*;
import io.grpc.Status;
import org.mapstruct.*;
import org.springframework.data.domain.Page;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface CommonGrpcMapper {

    default Meta toProto(Page<?> page) {
        return Meta.newBuilder()
                .setPage(page.getNumber() + 1)
                .setTotalCount(page.getTotalElements())
                .setPageSize(page.getSize())
                .setTotalPages(page.getTotalPages())
                .setHasNext(page.hasNext())
                .setHasPrevious(page.hasPrevious())
                .build();
    }

    @Named("uuidToString")
    default String uuidToString(UUID uuid) {
        return uuid != null ? uuid.toString() : "";
    }

    @Named("stringToUuid")
    default UUID stringToUuid(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw Status.INVALID_ARGUMENT
                    .withDescription("ID cannot be empty")
                    .asRuntimeException();
        }
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new EmptyIdException();
        }
    }

    @Named("nullToEmptyString")
    default String nullToEmptyString(String value) {
        return value != null ? value : "";
    }

    @Named("emptyStringToNull")
    default String emptyStringToNull(String value) {
        return value != null && !value.isEmpty() ? value : null;
    }

    @Named("nullToZeroDouble")
    default double nullToZeroDouble(Double value) {
        return value != null ? value : 0.0;
    }

    @Named("nullToZeroInt")
    default int nullToZeroInt(Integer value) {
        return value != null ? value : 0;
    }

    default com.margosha.kse.calories.proto.common.Date localDateToProtoDate(LocalDate date) {
        if (date == null) return null;
        return com.margosha.kse.calories.proto.common.Date.newBuilder()
                .setYear(date.getYear())
                .setMonth(date.getMonthValue())
                .setDay(date.getDayOfMonth())
                .build();
    }

    default LocalDate protoDateToLocalDate(com.margosha.kse.calories.proto.common.Date date) {
        if (date == null) return null;
        return LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
    }

    default Timestamp localDateTimeToTimestamp(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return Timestamp.newBuilder()
                .setSeconds(dateTime.toEpochSecond(java.time.ZoneOffset.UTC))
                .setNanos(dateTime.getNano())
                .build();
    }

    default LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) return null;
        return LocalDateTime.ofEpochSecond(
                timestamp.getSeconds(),
                timestamp.getNanos(),
                java.time.ZoneOffset.UTC
        );
    }
}