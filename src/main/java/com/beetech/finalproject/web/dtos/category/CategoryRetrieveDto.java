package com.beetech.finalproject.web.dtos.category;

import com.beetech.finalproject.web.dtos.image.ImageRetrieveDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryRetrieveDto {
    private Long id;
    private String name;
    private List<ImageRetrieveDto> images;
}
