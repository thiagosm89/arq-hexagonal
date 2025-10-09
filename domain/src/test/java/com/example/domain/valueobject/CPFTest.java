package com.example.domain.valueobject;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para Value Object CPF
 */
class CPFTest {
    
    private static final String CPF_VALIDO = "12345678909";
    private static final String CPF_VALIDO_FORMATADO = "123.456.789-09";
    
    @Test
    void deveCriarCPFValido() {
        CPF cpf = CPF.of(CPF_VALIDO);
        
        assertEquals(CPF_VALIDO, cpf.getValue());
    }
    
    @Test
    void deveCriarCPFComFormatacao() {
        CPF cpf = CPF.of(CPF_VALIDO_FORMATADO);
        
        assertEquals(CPF_VALIDO, cpf.getValue());
    }
    
    @Test
    void deveRemoverFormatacao() {
        CPF cpf = CPF.of("123.456.789-09");
        
        assertEquals("12345678909", cpf.getValue());
    }
    
    @Test
    void deveLancarExcecaoParaCPFNulo() {
        assertThrows(IllegalArgumentException.class, () -> {
            CPF.of(null);
        });
    }
    
    @Test
    void deveLancarExcecaoParaCPFVazio() {
        assertThrows(IllegalArgumentException.class, () -> {
            CPF.of("");
        });
    }
    
    @Test
    void deveLancarExcecaoParaCPFComMenos11Digitos() {
        assertThrows(IllegalArgumentException.class, () -> {
            CPF.of("123456789");
        });
    }
    
    @Test
    void deveLancarExcecaoParaCPFComMais11Digitos() {
        assertThrows(IllegalArgumentException.class, () -> {
            CPF.of("123456789012");
        });
    }
    
    @Test
    void deveLancarExcecaoParaCPFComTodosDigitosIguais() {
        assertThrows(IllegalArgumentException.class, () -> {
            CPF.of("11111111111");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            CPF.of("000.000.000-00");
        });
    }
    
    @Test
    void deveLancarExcecaoParaCPFComDigitoVerificadorInvalido() {
        assertThrows(IllegalArgumentException.class, () -> {
            CPF.of("12345678900"); // Dígito verificador inválido
        });
    }
    
    @Test
    void deveFormatarCPF() {
        CPF cpf = CPF.of(CPF_VALIDO);
        
        assertEquals("123.456.789-09", cpf.getFormatted());
    }
    
    @Test
    void deveMascararCPF() {
        CPF cpf = CPF.of(CPF_VALIDO);
        
        assertTrue(cpf.getMasked().contains("***"));
        assertTrue(cpf.getMasked().contains("789-09"));
    }
    
    @Test
    void deveSerIgualQuandoValorIgual() {
        CPF cpf1 = CPF.of("123.456.789-09");
        CPF cpf2 = CPF.of("12345678909");
        
        assertEquals(cpf1, cpf2);
        assertEquals(cpf1.hashCode(), cpf2.hashCode());
    }
    
    @Test
    void naoDeveSerIgualQuandoValorDiferente() {
        CPF cpf1 = CPF.of("12345678909");
        CPF cpf2 = CPF.of("98765432100");
        
        assertNotEquals(cpf1, cpf2);
    }
    
    @Test
    void deveRetornarStringFormatado() {
        CPF cpf = CPF.of(CPF_VALIDO);
        
        assertEquals("123.456.789-09", cpf.toString());
    }
    
    @Test
    void deveValidarCPFsConhecidos() {
        // CPFs válidos conhecidos
        assertDoesNotThrow(() -> CPF.of("111.444.777-35"));
        assertDoesNotThrow(() -> CPF.of("123.456.789-09"));
    }
}

