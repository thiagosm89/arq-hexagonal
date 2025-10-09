package com.example.domain.valueobject;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para Value Object Email
 */
class EmailTest {
    
    @Test
    void deveCriarEmailValido() {
        Email email = Email.of("joao@example.com");
        
        assertEquals("joao@example.com", email.getValue());
    }
    
    @Test
    void deveNormalizarEmail() {
        // Email com espaços e maiúsculas deve ser normalizado
        Email email = Email.of("  JOAO@EXAMPLE.COM  ");
        
        assertEquals("joao@example.com", email.getValue());
    }
    
    @Test
    void deveLancarExcecaoParaEmailNulo() {
        assertThrows(IllegalArgumentException.class, () -> {
            Email.of(null);
        });
    }
    
    @Test
    void deveLancarExcecaoParaEmailVazio() {
        assertThrows(IllegalArgumentException.class, () -> {
            Email.of("");
        });
    }
    
    @Test
    void deveLancarExcecaoParaEmailInvalido() {
        assertThrows(IllegalArgumentException.class, () -> {
            Email.of("email-invalido");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            Email.of("@example.com");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            Email.of("joao@");
        });
    }
    
    @Test
    void deveExtrairDominio() {
        Email email = Email.of("joao@example.com");
        
        assertEquals("example.com", email.getDomain());
    }
    
    @Test
    void deveExtrairLocalPart() {
        Email email = Email.of("joao.silva@example.com");
        
        assertEquals("joao.silva", email.getLocalPart());
    }
    
    @Test
    void deveVerificarDominio() {
        Email email = Email.of("joao@example.com");
        
        assertTrue(email.isFromDomain("example.com"));
        assertFalse(email.isFromDomain("other.com"));
    }
    
    @Test
    void deveSerIgualQuandoValorIgual() {
        Email email1 = Email.of("joao@example.com");
        Email email2 = Email.of("joao@example.com");
        
        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }
    
    @Test
    void naoDeveSerIgualQuandoValorDiferente() {
        Email email1 = Email.of("joao@example.com");
        Email email2 = Email.of("maria@example.com");
        
        assertNotEquals(email1, email2);
    }
    
    @Test
    void deveRetornarStringDoValor() {
        Email email = Email.of("joao@example.com");
        
        assertEquals("joao@example.com", email.toString());
    }
}

