package com.beetech.finalproject.validate.username;

import com.beetech.finalproject.validate.password.PasswordValidate;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UsernameValid.class)
public @interface ValidUsername {
    String message() default "{ValidUsername}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
