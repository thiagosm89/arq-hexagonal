package com.example.application.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisições de criação/atualização de usuário
 * 
 * Recebe Strings do cliente HTTP
 * Application converterá para Value Objects do Domain
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequest {
    private String nome;
    private String email;  // Será convertido para Email (Value Object)
    private String cpf;    // Será convertido para CPF (Value Object) - opcional
}

