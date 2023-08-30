package com.beetech.finalproject.web.dtos.user;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserRetrieveDto {
    private String userId;
    private String username;
    private String loginId;
    private LocalDate birthDay;
    private Double totalPrice;
}
