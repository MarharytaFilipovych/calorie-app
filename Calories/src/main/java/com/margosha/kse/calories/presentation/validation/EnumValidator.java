package com.margosha.kse.calories.presentation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumValidator implements ConstraintValidator<CorrectEnum, Enum<?>> {
    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(CorrectEnum constraintAnnotation) {
        enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if(value == null)return false;
        return value.getClass() == enumClass;
    }
}
