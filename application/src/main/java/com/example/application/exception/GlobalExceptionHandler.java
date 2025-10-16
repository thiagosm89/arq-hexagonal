package com.example.application.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler Global de Exceções
 * Trata as exceções de domínio e converte em respostas HTTP apropriadas
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioNaoEncontrado(ApiException ex) {
        ResponseErrorCode code = ex.getCode();

        Map<String, Object> body = errorBuild(
                code.getHttpStatus(),
                code.getErrorKey(),
                ex.getMessage()
        );
        
        return ResponseEntity.status(code.getHttpStatus()).body(body);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        ResponseErrorCode internalServerError = ResponseErrorCode.INTERNAL_SERVER_ERROR;

        String message = "Erro inesperado ocorreu. Nossa equipe está trabalhando nisso no momento.";

        Map<String, Object> body = errorBuild(
                internalServerError.getHttpStatus(),
                internalServerError.getErrorKey(),
                message
        );

        log.error(message, ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private Map<String, Object> errorBuild(HttpStatus httpStatus, String errorKey, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", httpStatus);
        body.put("error", errorKey);
        body.put("message", message);
        return body;
    }
}

