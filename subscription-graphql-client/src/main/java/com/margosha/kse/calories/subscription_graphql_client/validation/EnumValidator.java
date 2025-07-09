package com.margosha.kse.calories.subscription_graphql_client.validation;

import com.margosha.kse.calories.subscription_graphql_client.annotation.CorrectEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

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
