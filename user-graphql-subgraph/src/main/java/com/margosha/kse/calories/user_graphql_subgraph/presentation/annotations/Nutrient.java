package com.margosha.kse.calories.user_graphql_subgraph.presentation.annotations;

import com.margosha.kse.calories.user_graphql_subgraph.presentation.validation.NutrientValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NutrientValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Nutrient {
    String nutrient();
    String measurement() default "grams";
    String message() default "Nutrient value must be between 0 and 100";;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
