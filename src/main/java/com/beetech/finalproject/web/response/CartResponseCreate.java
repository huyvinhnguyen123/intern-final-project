package com.beetech.finalproject.web.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseCreate {
    private String token;
    private Double totalPrice;
    private Double versionNo;
}
