package com.beetech.finalproject.web.dtos.email;

import com.beetech.finalproject.web.dtos.image.ImageRetrieveDto;
import lombok.Data;

import java.util.List;

@Data
public class ProductOrigin {
    private String sku;
    private String productName;
    private String detailInfo;
    private Double price;
    private String categoryName;
    private String thumbnailImage;
    private List<String> detailImages;
}
