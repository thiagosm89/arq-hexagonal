package com.example.application.service.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO específico para listagens de usuários
 * Pode conter apenas os campos necessários para a listagem
 * (otimização de queries)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioListResponse {
    private Long id;
    private String nome;
    private String email;
    
    // Poderia ter campos adicionais para listagem:
    // private LocalDateTime dataCriacao;
    // private Boolean ativo;
    // etc.
}

