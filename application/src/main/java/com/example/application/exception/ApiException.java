package com.example.application.exception;

import lombok.Getter;

public class ApiException extends RuntimeException {

    @Getter
    private ResponseErrorCode code;

    public ApiException(ResponseErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

}
