package com.beetech.finalproject.web.response;

import com.beetech.finalproject.web.dtos.order.OrderRetrieveCreateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseCreate {
    private int displayId;
    private double totalPrice;
}
