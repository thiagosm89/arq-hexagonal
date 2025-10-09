package com.example.application.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respostas contendo dados de usuário
 * 
 * Retorna Strings para o cliente HTTP
 * Application converte Value Objects → Strings
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String nome;
    private String email;       // Vem de Email (Value Object)
    private String cpf;          // Vem de CPF (Value Object) - pode ser null
    private String cpfFormatado; // CPF formatado para exibição
}

