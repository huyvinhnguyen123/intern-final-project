package com.beetech.finalproject.web.dtos.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSearchInputDto {
    private Long categoryId;
    private String sku;
    private String productName;
}
