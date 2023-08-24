package com.beetech.finalproject.validate.username;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameValid implements ConstraintValidator<ValidUsername, String> {
    private static final String CONTENT_VALID_MESSAGE = "{ValidUsername.number.format}";
    private static final String NULL_VALID_MESSAGE = "{User.username.notNul}";
    private static final String EMPTY_VALID_MESSAGE = "{User.username.notEmpty}";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null) {
            addErrorMessage(context, NULL_VALID_MESSAGE);
            return false;
        }

        if(value.isEmpty()) {
            addErrorMessage(context, EMPTY_VALID_MESSAGE);
            return false;
        }

        char firstCharacter = value.charAt(0);
        if(firstCharacter >= '0' && firstCharacter < '9') {
            addErrorMessage(context, CONTENT_VALID_MESSAGE);
            return false;
        }

        return true;
    }

    private void addErrorMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
