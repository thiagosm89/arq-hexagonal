package com.example.application.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ResponseErrorCode {

    USUARIO_NAO_ENCONTRADO(400, "USER_NOT_FOUND"),
    INVALID_REQUEST(400, "INVALID_REQUEST"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    FORBIDDEN(403, "FORBIDDEN"),
    NOT_FOUND(404, "NOT_FOUND"),
    CONFLICT(409, "CONFLICT"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR");

    private int httpStatus;
    private String errorKey;

    public HttpStatus getHttpStatus() {
        return HttpStatus.valueOf(httpStatus);
    }

    public String getErrorKey() {
        return errorKey;
    }

}