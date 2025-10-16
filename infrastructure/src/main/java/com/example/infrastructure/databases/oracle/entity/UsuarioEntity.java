package com.example.infrastructure.databases.oracle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade JPA - Representação do Usuário no banco de dados
 * 
 * IMPORTANTE: Esta é uma entidade de INFRAESTRUTURA (JPA)
 * Os Value Objects (Email, CPF) do Domain são convertidos para Strings aqui
 * pois o banco de dados armazena strings, não objetos
 */
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(nullable = false, unique = true)
    private String email; // String aqui, mas Email (Value Object) no Domain
    
    @Column(nullable = true, length = 11)
    private String cpf; // String aqui, mas CPF (Value Object) no Domain
}

