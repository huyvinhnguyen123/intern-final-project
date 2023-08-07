package com.beetech.finalproject.web.dtos.category;

import com.beetech.finalproject.web.dtos.image.ImageRetrieveDto;
import lombok.Data;

import java.util.List;

@Data
public class CategoryRetrieveDto {
    private Long id;
    private String name;
    private List<ImageRetrieveDto> images;
}
