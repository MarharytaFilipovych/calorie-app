package com.margosha.kse.calories.user_graphql_subgraph.presentation.validation;

import com.margosha.kse.calories.user_graphql_subgraph.presentation.annotations.CorrectEnum;
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
