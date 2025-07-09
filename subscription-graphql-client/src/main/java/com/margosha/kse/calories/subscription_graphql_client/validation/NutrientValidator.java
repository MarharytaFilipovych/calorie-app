package com.margosha.kse.calories.subscription_graphql_client.validation;

import com.margosha.kse.calories.subscription_graphql_client.annotation.Nutrient;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NutrientValidator implements ConstraintValidator<Nutrient, Double> {
    private String nutrient;
    private String measurement;

    @Override
    public void initialize(Nutrient constraintAnnotation) {
        nutrient = constraintAnnotation.nutrient();
        measurement = constraintAnnotation.measurement();
    }

    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        boolean correct = (value != null && value >= 0 && value <=100);
        if(!correct){
            context.disableDefaultConstraintViolation();
            String dynamicMessage = String.format("%s is required. Its value must be between 0 and 100 %s", nutrient, measurement);
            context.buildConstraintViolationWithTemplate(dynamicMessage)
                    .addConstraintViolation();
        }
        return correct;
    }
}
