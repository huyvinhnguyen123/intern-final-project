package com.beetech.finalproject.web.dtos.statistic;

import lombok.Data;

@Data
public class ProductStatisticDto {
    private Long productId;
    private String sku;
    private String productName;
    private Double price;
    private Long viewsCount;
    private Long likesCount;
    private Long dislikesCount;
    private String statisticDate;
}
