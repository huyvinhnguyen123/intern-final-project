package com.beetech.finalproject.validate.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidate implements ConstraintValidator<ValidPassword, String> {
    private static final int MIN_LENGTH = 6;
    private static final int MAX_LENGTH = 32;
    private static final String SIZE_VALID_MESSAGE = "{ValidPassword.size}";
    private static final String CONTENT_VALID_MESSAGE = "{ValidPassword.content}";

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (!isLengthValid(password)) {
            addErrorMessage(context, SIZE_VALID_MESSAGE);
            return false;
        }

        if (!isContentValid(password)) {
            addErrorMessage(context, CONTENT_VALID_MESSAGE);
            return false;
        }

        return true;
    }

    private boolean isLengthValid(String password) {
        int length = password.length();
        return length >= MIN_LENGTH && length <= MAX_LENGTH;
    }

    private boolean isContentValid(String password) {
        // checking if the password contains at least
        // one lowercase letter,
        // one uppercase letter,
        // and one digit
        return password.matches(".*[a-z].*")
                && password.matches(".*[A-Z].*")
                && password.matches(".*\\d.*");
    }

    private void addErrorMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
