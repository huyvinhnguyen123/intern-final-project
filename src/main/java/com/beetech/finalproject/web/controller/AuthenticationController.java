package com.beetech.finalproject.web.controller;

import com.beetech.finalproject.common.AuthException;
import com.beetech.finalproject.common.DeleteFlag;
import com.beetech.finalproject.common.ValidationException;
import com.beetech.finalproject.domain.entities.User;
import com.beetech.finalproject.domain.service.UserService;
import com.beetech.finalproject.web.common.ResponseDto;
import com.beetech.finalproject.web.dtos.user.UserCreateDto;
import com.beetech.finalproject.web.dtos.user.UserLoginDto;
import com.beetech.finalproject.web.response.LoginResponse;
import com.beetech.finalproject.web.security.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils JwtUtils;
    private final UserService userService;

    /**
     * Validate field
     *
     * @param bindingResult - when user input wrong condition
     * @param fieldErrors - input field error
     * @return - list validate field error
     */
    public Map<String, String> validateFields(BindingResult bindingResult, Map<String, String> fieldErrors) {
        for(FieldError fieldError: bindingResult.getFieldErrors()) {
            String fieldName = fieldError.getField();
            String errorMessage = fieldError.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        }
        throw new ValidationException(fieldErrors);
    }

    /**
     * request create user
     *
     * @param userCreateDto - input user information
     * @return - token when use is created
     */
    @PostMapping("/register")
    public ResponseEntity<ResponseDto<Object>> createUser(@Valid @RequestBody UserCreateDto userCreateDto,
                                                          BindingResult bindingResult) {
        log.info("Request creating user...");

        // Check for validation errors in the input
        if (bindingResult.hasErrors()) {
            Map<String, String> fieldErrors = new HashMap<>();
            validateFields(bindingResult, fieldErrors);
        }

        userService.registerNewUser(userCreateDto);
        return ResponseEntity.ok(ResponseDto.build().withMessage("OK"));
    }

    /**
     * request login authentication
     *
     * @param userLoginDto - input user login information
     * @return - token when authentication is passed
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<Object>> login(@Valid @RequestBody UserLoginDto userLoginDto,
                                                     BindingResult bindingResult) {
        log.info("Request authenticating user...");

        // Check for validation errors in the input
        if (bindingResult.hasErrors()) {
            Map<String, String> fieldErrors = new HashMap<>();
            validateFields(bindingResult, fieldErrors);
        }

        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userLoginDto.getLoginId(),
                    userLoginDto.getPassword()
            );

            Authentication login = authenticationManager.authenticate(authentication);

            // Check if the user is deleted or locked
            User user = (User) login.getPrincipal();
            if (DeleteFlag.DELETED.getCode() == user.getDeleteFlag() || !user.isAccountNonLocked()) {
                log.error("User has been deleted or locked.");
                throw new AuthException(AuthException.ErrorStatus.USER_DO_NOT_HAVE_PERMISSION);
            }

            String token = JwtUtils.createToken(user);
            log.info("Create token success: {}", token);
            String refreshToken = UUID.randomUUID().toString();
            log.info("Create refresh token success: {}", refreshToken);

            LoginResponse loginResponse = LoginResponse.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .build();

            log.info("Login successfully");
            return ResponseEntity.ok(ResponseDto.build().withData(loginResponse));
        } catch (AuthenticationException e) {
            log.error("authentication failed: " + e.getMessage());
            throw new AuthException(AuthException.ErrorStatus.INVALID_GRANT);
        }
    }
}
