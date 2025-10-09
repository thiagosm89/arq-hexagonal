package com.example.domain.exception;

/**
 * Exceção de Domínio para Validação
 */
public class UsuarioInvalidoException extends RuntimeException {
    public UsuarioInvalidoException(String message) {
        super(message);
    }
}

