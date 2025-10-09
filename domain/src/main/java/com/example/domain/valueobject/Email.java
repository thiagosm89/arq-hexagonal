package com.example.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object - Email
 * 
 * Características de um Value Object:
 * 1. Imutável (final, sem setters)
 * 2. Validação no construtor
 * 3. Equals e hashCode baseados no valor
 * 4. Sem identidade própria (é o valor)
 * 5. Substitui primitivos/Strings com regras de negócio
 */
public final class Email {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private final String value;
    
    /**
     * Construtor privado - força uso do factory method
     */
    private Email(String value) {
        this.value = value;
    }
    
    /**
     * Factory method para criar Email
     * Contém validação de regra de negócio
     */
    public static Email of(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email não pode ser nulo ou vazio");
        }
        
        String normalizedEmail = email.trim().toLowerCase();
        
        if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            throw new IllegalArgumentException("Email inválido: " + email);
        }
        
        return new Email(normalizedEmail);
    }
    
    /**
     * Retorna o valor do email
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Retorna o domínio do email (ex: "example.com" de "user@example.com")
     */
    public String getDomain() {
        return value.substring(value.indexOf('@') + 1);
    }
    
    /**
     * Retorna o usuário do email (ex: "user" de "user@example.com")
     */
    public String getLocalPart() {
        return value.substring(0, value.indexOf('@'));
    }
    
    /**
     * Verifica se é um email de um domínio específico
     */
    public boolean isFromDomain(String domain) {
        return getDomain().equalsIgnoreCase(domain);
    }
    
    /**
     * Value Objects são iguais se seus valores são iguais
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    /**
     * Representação string do Value Object
     */
    @Override
    public String toString() {
        return value;
    }
}

