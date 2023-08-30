package com.beetech.finalproject.web.dtos.category;

import com.beetech.finalproject.validate.image.ValidImage;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
public class CategoryUpdateDto {
    @NotNull(message = "{Category.categoryName.notNull}")
    @NotEmpty(message = "{Category.categoryName.notEmpty}")
    private String categoryName;

    @ValidImage
    private MultipartFile image;
}
