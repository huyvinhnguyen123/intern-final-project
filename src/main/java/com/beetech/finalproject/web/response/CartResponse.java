package com.beetech.finalproject.web.response;

import com.beetech.finalproject.web.dtos.cart.CartDetailRetrieveDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
    private Long cartId;
    private Double totalPrice;
    private Double versionNo;
    private List<CartDetailRetrieveDto> details;
}
