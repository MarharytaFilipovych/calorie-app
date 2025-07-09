package com.margosha.kse.calories.subscription_graphql_client.annotation;

import com.margosha.kse.calories.subscription_graphql_client.validation.NameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NameValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface CorrectName {
    boolean required() default true;
    String message() default "Incorrect name format!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
