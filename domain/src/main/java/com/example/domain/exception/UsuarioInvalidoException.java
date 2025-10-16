package com.example.domain.exception;

/**
 * Exceção de Domínio para Validação
 */
public class UsuarioInvalidoException extends Exception {
    public UsuarioInvalidoException(String message) {
        super(message);
    }
}

