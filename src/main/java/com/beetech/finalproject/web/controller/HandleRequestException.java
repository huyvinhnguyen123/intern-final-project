package com.beetech.finalproject.web.controller;

import com.beetech.finalproject.web.common.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

@RestControllerAdvice
public class HandleRequestException {

    /**
     * handle bad request for error system
     *
     * @param errorMessage - input error message
     * @return -  bad request body
     */
    @ExceptionHandler({ AuthenticationException.class, GeneralSecurityException.class, IOException.class })
    public static ResponseEntity<ResponseDto<Object>> handleRequest(HttpStatus httpStatus, String errorMessage) {
        ResponseDto<Object> responseDto = ResponseDto.build().withHttpStatus(httpStatus).withMessage(errorMessage);
        return ResponseEntity.badRequest().body(responseDto);
    }

    /**
     * handle bad request for error validate input
     *
     * @param defaultErrorMessage - input error message
     * @param fieldErrors - input list field error
     * @param bindingResult - input binding result
     * @return -  bad request body
     */
    @ExceptionHandler(ValidationException.class)
    public static ResponseEntity<ResponseDto<Object>> handleRequest(HttpStatus httpStatus, String defaultErrorMessage,
                                                                    Map<String, String> fieldErrors, BindingResult bindingResult) {
        // show fields and message error
        for(FieldError fieldError: bindingResult.getFieldErrors()) {
            String fieldName = fieldError.getField();
            String errorMessage = fieldError.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        }
        // return dto response status, message and data
        ResponseDto<Object> responseDto = ResponseDto.build().withHttpStatus(httpStatus)
                .withMessage(defaultErrorMessage)
                .withData(fieldErrors);
        return ResponseEntity.badRequest().body(responseDto);
    }
}
