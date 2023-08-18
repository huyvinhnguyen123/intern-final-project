package com.beetech.finalproject.validate.image;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImageValid.class)
public @interface ValidImage {
    String message() default "{ValidImage}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}