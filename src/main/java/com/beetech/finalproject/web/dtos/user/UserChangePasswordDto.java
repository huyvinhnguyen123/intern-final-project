package com.beetech.finalproject.web.dtos.user;

import com.beetech.finalproject.validate.password.ValidPassword;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserChangePasswordDto {
    @NotNull(message = "{User.password.notNull}")
    @NotEmpty(message = "{User.password.notEmpty}")
    @ValidPassword
    private String oldPassword;

    @NotNull(message = "{User.password.notNull}")
    @NotEmpty(message = "{User.password.notEmpty}")
    @ValidPassword
    private String password;
}
