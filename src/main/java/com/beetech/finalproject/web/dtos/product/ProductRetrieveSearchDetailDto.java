package com.beetech.finalproject.web.dtos.product;

import com.beetech.finalproject.web.dtos.image.ImageRetrieveDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductRetrieveSearchDetailDto {
    private Long productId;
    private String sku;
    private String productName;
    private String detailInfo;
    private Double price;
    private List<ImageRetrieveDto> images;
}
