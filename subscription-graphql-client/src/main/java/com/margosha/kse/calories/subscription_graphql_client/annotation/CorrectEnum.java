package com.margosha.kse.calories.subscription_graphql_client.annotation;

import com.margosha.kse.calories.subscription_graphql_client.validation.EnumValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidator.class)
public @interface CorrectEnum {
    Class<? extends Enum<?>> enumClass();
    String message() default "Incorrect enum value! Must be any of {enumClass}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
