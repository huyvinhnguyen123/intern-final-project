package com.beetech.finalproject.web.dtos.product;

import lombok.Data;

@Data
public class ProductDetailDto {
    private Long productId;
    private String sku;
    private String productName;
    private String image;
    private String path;
    private Double price;
    private String detailInfo;
    private Long likesCount;
    private Long dislikesCount;
    private Long viewsCount;
}
