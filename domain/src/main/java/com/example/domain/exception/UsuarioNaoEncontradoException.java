package com.example.domain.exception;

/**
 * Exceção de Domínio
 */
public class UsuarioNaoEncontradoException extends Exception {
    public UsuarioNaoEncontradoException(Long id) {
        super("Usuário não encontrado com id: " + id);
    }
    
    public UsuarioNaoEncontradoException(String message) {
        super(message);
    }
}

