package com.beetech.finalproject.web.dtos.user;

import com.beetech.finalproject.validate.birthday.ValidBirthDay;
import com.beetech.finalproject.validate.password.ValidPassword;
import com.beetech.finalproject.validate.username.ValidUsername;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDto {
    @ValidUsername
    private String username;

    @NotNull(message = "{User.birthDay.notNull}")
    @NotEmpty(message = "{User.birthDay.notEmpty}")
    @ValidBirthDay
    private String birthDay;

    @NotNull(message = "{User.loginId.notNull}")
    @NotEmpty(message = "{User.loginId.notEmpty}")
    @Email
    private String loginId; // loginId = email

    @NotNull(message = "{User.password.notNull}")
    @NotEmpty(message = "{User.password.notEmpty}")
    @ValidPassword
    private String password;
}
