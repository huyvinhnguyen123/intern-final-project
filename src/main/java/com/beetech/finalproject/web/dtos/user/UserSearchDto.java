package com.beetech.finalproject.web.dtos.user;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserSearchDto {
    private String username;
    private String loginId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalPrice;
}
