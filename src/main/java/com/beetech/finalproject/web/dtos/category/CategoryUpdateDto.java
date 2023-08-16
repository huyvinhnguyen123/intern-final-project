package com.beetech.finalproject.web.dtos.category;

import com.beetech.finalproject.validate.image.ValidImage;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class CategoryUpdateDto {
    @NotNull(message = "{Category.categoryName.notNull}")
    @NotEmpty(message = "{Category.categoryName.notEmpty}")
    private String categoryName;

    @NotNull(message = "{Image.notNull}")
    @NotEmpty(message = "{Image.notEmpty}")
    @ValidImage
    private MultipartFile image;
}
