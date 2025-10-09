package com.example.domain.valueobject;

import java.util.Objects;

/**
 * Value Object - CPF (Cadastro de Pessoa Física)
 * 
 * Encapsula a lógica de validação do CPF no próprio objeto
 * Garante que um CPF sempre está válido se existe
 * 
 * Características de um Value Object:
 * 1. Imutável
 * 2. Auto-validável
 * 3. Sem identidade (é o valor)
 * 4. Equals baseado no valor
 */
public final class CPF {
    
    private final String value;
    
    /**
     * Construtor privado - força uso do factory method
     */
    private CPF(String value) {
        this.value = value;
    }
    
    /**
     * Factory method para criar CPF
     * Valida e normaliza o CPF
     */
    public static CPF of(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF não pode ser nulo ou vazio");
        }
        
        // Remove formatação (pontos, hífen, espaços)
        String cleanCpf = cpf.replaceAll("[^0-9]", "");
        
        if (cleanCpf.length() != 11) {
            throw new IllegalArgumentException("CPF deve ter 11 dígitos");
        }
        
        // Verifica se todos os dígitos são iguais (ex: 111.111.111-11)
        if (cleanCpf.matches("(\\d)\\1{10}")) {
            throw new IllegalArgumentException("CPF inválido: todos os dígitos são iguais");
        }
        
        // Valida dígitos verificadores
        if (!isValidCPF(cleanCpf)) {
            throw new IllegalArgumentException("CPF inválido: " + cpf);
        }
        
        return new CPF(cleanCpf);
    }
    
    /**
     * Algoritmo de validação do CPF
     */
    private static boolean isValidCPF(String cpf) {
        try {
            // Calcula o primeiro dígito verificador
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int firstDigit = 11 - (sum % 11);
            firstDigit = (firstDigit >= 10) ? 0 : firstDigit;
            
            if (firstDigit != Character.getNumericValue(cpf.charAt(9))) {
                return false;
            }
            
            // Calcula o segundo dígito verificador
            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            int secondDigit = 11 - (sum % 11);
            secondDigit = (secondDigit >= 10) ? 0 : secondDigit;
            
            return secondDigit == Character.getNumericValue(cpf.charAt(10));
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Retorna o CPF sem formatação (apenas números)
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Retorna o CPF formatado (XXX.XXX.XXX-XX)
     */
    public String getFormatted() {
        return String.format("%s.%s.%s-%s",
            value.substring(0, 3),
            value.substring(3, 6),
            value.substring(6, 9),
            value.substring(9, 11)
        );
    }
    
    /**
     * Retorna CPF mascarado para exibição (XXX.XXX.XXX-XX)
     */
    public String getMasked() {
        return String.format("***.***. %s-%s",
            value.substring(6, 9),
            value.substring(9, 11)
        );
    }
    
    /**
     * Value Objects são iguais se seus valores são iguais
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CPF cpf = (CPF) o;
        return Objects.equals(value, cpf.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    /**
     * Representação string (formatado para melhor legibilidade)
     */
    @Override
    public String toString() {
        return getFormatted();
    }
}

