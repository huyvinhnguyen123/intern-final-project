package com.beetech.finalproject.web.response;

import com.beetech.finalproject.web.dtos.product.ProductDetailDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailStatisticResponse {
    private ProductDetailDto productDetailDto;
}
