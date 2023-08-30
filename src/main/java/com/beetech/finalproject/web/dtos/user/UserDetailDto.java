package com.beetech.finalproject.web.dtos.user;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserDetailDto {
    private String userId;
    private String username;
    private LocalDate birthDay;
}
