package com.beetech.finalproject.common;

import lombok.Getter;
import org.springframework.validation.BindingResult;

import java.io.Serial;
import java.util.Map;

@Getter
public class ValidationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 6785420639768008206L;
    private BindingResult bindingResult;
    private String detailMessage;
    private Map<String, String> fieldErrors;

    public ValidationException(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public ValidationException(String message, BindingResult bindingResult, Map<String, String> fieldErrors) {
        super(message);
        this.bindingResult = bindingResult;
        this.fieldErrors = fieldErrors;
    }

    public ValidationException(BindingResult bindingResult) {
        super();
        this.bindingResult = bindingResult;
    }

    public ValidationException(String message, String detailMessage) {
        super(message);
        this.detailMessage = detailMessage;
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }

    public void setBindingResult(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    public void setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
    }
}
