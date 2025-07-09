package com.margosha.kse.calories.config;

import graphql.GraphQLContext;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.scalars.ExtendedScalars;
import graphql.schema.*;
import graphql.execution.CoercedVariables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

@Configuration
@Slf4j
public class GraphQLConfig {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter SIMPLE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter WITH_SECONDS_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final GraphQLScalarType LOCAL_DATE_TIME = GraphQLScalarType.newScalar()
            .name("LocalDateTime")
            .description("A custom scalar that handles LocalDateTime")
            .coercing(new Coercing<LocalDateTime, String>() {
                @Override
                public String serialize(Object dataFetcherResult, GraphQLContext graphQLContext, Locale locale) {
                    return serializeLocalDateTime(dataFetcherResult);
                }

                @Override
                public LocalDateTime parseValue(Object input, GraphQLContext graphQLContext, Locale locale) {
                    return parseLocalDateTimeFromVariable(input);
                }

                @Override
                public LocalDateTime parseLiteral(Value input, CoercedVariables variables, GraphQLContext graphQLContext, Locale locale) {
                    return parseLocalDateTimeFromAstLiteral(input);
                }
            })
            .build();

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer(){
        return wiringBuilder -> wiringBuilder
                .scalar(ExtendedScalars.Date)
                .scalar(LOCAL_DATE_TIME);
    }

    private static String serializeLocalDateTime(Object dataFetcherResult) {
        if (dataFetcherResult instanceof LocalDateTime dateTime) {
            return ISO_FORMATTER.format(dateTime);
        }
        if (dataFetcherResult instanceof String) {
            // Validate the string format
            try {
                LocalDateTime.parse((String) dataFetcherResult);
                return (String) dataFetcherResult;
            } catch (DateTimeParseException e) {
                throw new CoercingSerializeException("Invalid date format: " + dataFetcherResult);
            }
        }
        throw new CoercingSerializeException("Expected a LocalDateTime object but got: " + dataFetcherResult.getClass());
    }

    private static LocalDateTime parseLocalDateTimeFromVariable(Object input) {
        if (input instanceof String dateTimeStr) {
            try {
                return tryParseDateTime(dateTimeStr);
            } catch (DateTimeParseException e) {
                throw new CoercingParseValueException(
                        "Invalid LocalDateTime format. Expected ISO format like '2024-01-15T10:30:00' or '2024-01-15 10:30'. Got: " + dateTimeStr, e);
            }
        }
        throw new CoercingParseValueException("Expected a String value for LocalDateTime but got: " + input.getClass());
    }

    private static LocalDateTime parseLocalDateTimeFromAstLiteral(Value<?> input) {
        if (input instanceof StringValue stringValue) {
            try {
                return tryParseDateTime(stringValue.getValue());
            } catch (DateTimeParseException e) {
                throw new CoercingParseLiteralException(
                        "Invalid LocalDateTime literal. Expected ISO format like '2024-01-15T10:30:00' or '2024-01-15 10:30'. Got: " + stringValue.getValue(), e);
            }
        }
        throw new CoercingParseLiteralException("Expected a StringValue for LocalDateTime literal but got: " + input.getClass());
    }

    private static LocalDateTime tryParseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr, ISO_FORMATTER);
        } catch (DateTimeParseException e1) {
            try {
                return LocalDateTime.parse(dateTimeStr, WITH_SECONDS_FORMATTER);
            } catch (DateTimeParseException e2) {
                try {
                    return LocalDateTime.parse(dateTimeStr, SIMPLE_FORMATTER);
                } catch (DateTimeParseException e3) {
                    throw new DateTimeParseException("Unable to parse LocalDateTime: " + dateTimeStr, dateTimeStr, 0);
                }
            }
        }
    }
}