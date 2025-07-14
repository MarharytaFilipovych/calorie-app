package com.margosha.kse.calories.presentation.validation;

import com.margosha.kse.calories.presentation.annotations.CorrectEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<CorrectEnum, Enum<?>> {
    private Class<? extends Enum<?>> enumClass;
    private boolean required;

    @Override
    public void initialize(CorrectEnum constraintAnnotation) {
        enumClass = constraintAnnotation.enumClass();
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if(value == null)return !required;
        return value.getClass() == enumClass;
    }
}
