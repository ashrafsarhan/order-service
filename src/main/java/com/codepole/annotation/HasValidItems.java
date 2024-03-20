package com.codepole.annotation;

import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy= ItemValidator.class)
public @interface HasValidItems {

    String message() default "Item does not exist in the database";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
