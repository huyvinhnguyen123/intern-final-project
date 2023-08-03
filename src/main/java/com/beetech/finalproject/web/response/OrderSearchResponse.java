package com.beetech.finalproject.web.response;

import com.beetech.finalproject.web.dtos.order.OrderDetailRetrieveDto;
import com.beetech.finalproject.web.dtos.order.OrderRetrieveSearchDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderSearchResponse {
    private Long orderId;
    private int displayId;
    private String username;
    private Double totalPrice;
    private LocalDate orderDate;
    private String orderStatus;
    private String shippingAddress;
    private String shippingDistrict;
    private String shippingCity;
    private String shippingPhoneNumber;
    private List<OrderDetailRetrieveDto> details;
}
