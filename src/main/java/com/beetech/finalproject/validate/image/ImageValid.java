package com.beetech.finalproject.validate.image;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class ImageValid implements ConstraintValidator<ValidImage, MultipartFile> {
    private static final String SIZE_VALID_MESSAGE = "{ValidImage.size}";
    private static final String TYPE_VALID_MESSAGE = "{ValidImage.type}";
    private static final String NULL_VALID_MESSAGE = "{Image.notNull}";
    private static final String EMPTY_VALID_MESSAGE = "{Image.notEmpty}";

    private static final long MAX_SIZE_IN_BYTES = (long) 2 * 1024 * 1024; // 2MB in bytes

    @Override
    public boolean isValid(MultipartFile image, ConstraintValidatorContext constraintValidatorContext) {
        if(image == null) {
            addErrorMessage(constraintValidatorContext, NULL_VALID_MESSAGE);
            return false;
        }

        if(image.isEmpty()) {
            addErrorMessage(constraintValidatorContext, EMPTY_VALID_MESSAGE);
            return false;
        }

        if (image.getSize() > MAX_SIZE_IN_BYTES) {
            addErrorMessage(constraintValidatorContext, SIZE_VALID_MESSAGE);
            return false;
        }

        if (!isTypeValid(image)) {
            addErrorMessage(constraintValidatorContext, TYPE_VALID_MESSAGE);
            return false;
        }

        return true;
    }

    private boolean isTypeValid(MultipartFile image) {
        String imageName = image.getOriginalFilename();
        assert imageName != null;
        String fileExtension = imageName.substring(imageName.lastIndexOf(".") + 1);
        return fileExtension.equalsIgnoreCase("jpg");
    }

    private void addErrorMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
