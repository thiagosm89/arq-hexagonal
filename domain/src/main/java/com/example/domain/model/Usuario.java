package com.example.domain.model;

import com.example.domain.valueobject.CPF;
import com.example.domain.valueobject.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Entidade de Domínio - Representa um Usuário
 * 
 * Entidade possui:
 * - Identidade (id)
 * - Atributos mutáveis (nome)
 * - Value Objects (email, cpf) - imutáveis e auto-validáveis
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    
    private Long id;
    private String nome;
    private Email email;  // Value Object - sempre válido
    private CPF cpf;      // Value Object - sempre válido (pode ser null)
    
    /**
     * Construtor para criação sem ID (antes de persistir)
     */
    public Usuario(String nome, Email email, CPF cpf) {
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
    }
    
    /**
     * Construtor para criação sem CPF
     */
    public Usuario(String nome, Email email) {
        this(nome, email, null);
    }
    
    /**
     * Factory method para criar usuário a partir de Strings
     * Valida e converte para Value Objects
     */
    public static Usuario criar(String nome, String emailString, String cpfString) {
        Email email = Email.of(emailString);
        CPF cpf = cpfString != null && !cpfString.isBlank() 
            ? CPF.of(cpfString) 
            : null;
        return new Usuario(nome, email, cpf);
    }
    
    /**
     * Factory method para criar usuário sem CPF
     */
    public static Usuario criar(String nome, String emailString) {
        Email email = Email.of(emailString);
        return new Usuario(nome, email, null);
    }
    
    /**
     * Valida a entidade Usuario
     * Value Objects (email, cpf) já são válidos por construção
     */
    public boolean isValid() {
        // Nome é obrigatório
        if (nome == null || nome.isBlank()) {
            return false;
        }
        
        // Email é obrigatório e já é válido (Value Object)
        if (email == null) {
            return false;
        }
        
        // CPF é opcional, mas se existir já é válido (Value Object)
        return true;
    }
    
    /**
     * Atualiza o nome do usuário
     */
    public void atualizarNome(String novoNome) {
        if (novoNome == null || novoNome.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        this.nome = novoNome;
    }
    
    /**
     * Atualiza o email do usuário
     * Como Email é Value Object (imutável), substituímos por um novo
     */
    public void atualizarEmail(Email novoEmail) {
        if (novoEmail == null) {
            throw new IllegalArgumentException("Email não pode ser nulo");
        }
        this.email = novoEmail;
    }
    
    /**
     * Atualiza o email a partir de String
     */
    public void atualizarEmail(String emailString) {
        this.email = Email.of(emailString);
    }
    
    /**
     * Atualiza o CPF do usuário
     * Como CPF é Value Object (imutável), substituímos por um novo
     */
    public void atualizarCpf(CPF novoCpf) {
        this.cpf = novoCpf;
    }
    
    /**
     * Atualiza o CPF a partir de String
     */
    public void atualizarCpf(String cpfString) {
        this.cpf = cpfString != null && !cpfString.isBlank() 
            ? CPF.of(cpfString) 
            : null;
    }
    
    /**
     * Verifica se o usuário tem CPF cadastrado
     */
    public boolean temCpf() {
        return cpf != null;
    }
    
    /**
     * Retorna o email como String (para compatibilidade)
     */
    public String getEmailAsString() {
        return email != null ? email.getValue() : null;
    }
    
    /**
     * Retorna o CPF como String (para compatibilidade)
     */
    public String getCpfAsString() {
        return cpf != null ? cpf.getValue() : null;
    }
}

