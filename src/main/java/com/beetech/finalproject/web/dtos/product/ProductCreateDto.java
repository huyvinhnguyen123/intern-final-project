package com.beetech.finalproject.web.dtos.product;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ProductCreateDto {
    private String sku;
    private String productName;
    private String detailInfo;
    private Double price;
    private Long categoryId;
    private MultipartFile thumbnailImage;
    private List<MultipartFile> detailImages;
}
