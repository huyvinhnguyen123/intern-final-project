package com.beetech.finalproject.validate.birthday;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BirthDayValidate.class)
public @interface ValidBirthDay {
    String message() default "{ValidBirthDay}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

