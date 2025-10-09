package com.example.domain.model;

import com.example.domain.valueobject.CPF;
import com.example.domain.valueobject.Email;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a entidade Usuario
 * Agora usando Value Objects (Email e CPF)
 */
class UsuarioTest {
    
    @Test
    void deveCriarUsuarioComEmailECpf() {
        Email email = Email.of("joao@example.com");
        CPF cpf = CPF.of("123.456.789-09");
        Usuario usuario = new Usuario("João Silva", email, cpf);
        
        assertTrue(usuario.isValid());
        assertEquals("João Silva", usuario.getNome());
        assertEquals(email, usuario.getEmail());
        assertEquals(cpf, usuario.getCpf());
    }
    
    @Test
    void deveCriarUsuarioSemCpf() {
        Email email = Email.of("joao@example.com");
        Usuario usuario = new Usuario("João Silva", email);
        
        assertTrue(usuario.isValid());
        assertFalse(usuario.temCpf());
        assertNull(usuario.getCpf());
    }
    
    @Test
    void deveCriarUsuarioUsandoFactoryMethod() {
        Usuario usuario = Usuario.criar("João Silva", "joao@example.com", "123.456.789-09");
        
        assertTrue(usuario.isValid());
        assertEquals("João Silva", usuario.getNome());
        assertEquals("joao@example.com", usuario.getEmailAsString());
        assertEquals("12345678909", usuario.getCpfAsString());
    }
    
    @Test
    void deveCriarUsuarioSemCpfUsandoFactory() {
        Usuario usuario = Usuario.criar("João Silva", "joao@example.com");
        
        assertTrue(usuario.isValid());
        assertFalse(usuario.temCpf());
    }
    
    @Test
    void deveLancarExcecaoAoCriarComEmailInvalido() {
        assertThrows(IllegalArgumentException.class, () -> {
            Usuario.criar("João Silva", "email-invalido");
        });
    }
    
    @Test
    void deveLancarExcecaoAoCriarComCpfInvalido() {
        assertThrows(IllegalArgumentException.class, () -> {
            Usuario.criar("João Silva", "joao@example.com", "111.111.111-11");
        });
    }
    
    @Test
    void naoDeveValidarUsuarioComNomeNulo() {
        Email email = Email.of("joao@example.com");
        Usuario usuario = new Usuario(null, email);
        
        assertFalse(usuario.isValid());
    }
    
    @Test
    void naoDeveValidarUsuarioComNomeVazio() {
        Email email = Email.of("joao@example.com");
        Usuario usuario = new Usuario("", email);
        
        assertFalse(usuario.isValid());
    }
    
    @Test
    void naoDeveValidarUsuarioComEmailNulo() {
        Usuario usuario = new Usuario("João Silva", null);
        
        assertFalse(usuario.isValid());
    }
    
    @Test
    void deveAtualizarNome() {
        Usuario usuario = Usuario.criar("João Silva", "joao@example.com");
        
        usuario.atualizarNome("João Pedro Silva");
        
        assertEquals("João Pedro Silva", usuario.getNome());
    }
    
    @Test
    void deveAtualizarEmail() {
        Usuario usuario = Usuario.criar("João Silva", "joao@example.com");
        
        usuario.atualizarEmail("joao.novo@example.com");
        
        assertEquals("joao.novo@example.com", usuario.getEmailAsString());
    }
    
    @Test
    void deveAtualizarCpf() {
        Usuario usuario = Usuario.criar("João Silva", "joao@example.com");
        
        usuario.atualizarCpf("111.444.777-35");
        
        assertTrue(usuario.temCpf());
        assertEquals("11144477735", usuario.getCpfAsString());
    }
    
    @Test
    void deveVerificarSeTemCpf() {
        Usuario usuarioComCpf = Usuario.criar("João", "joao@test.com", "123.456.789-09");
        Usuario usuarioSemCpf = Usuario.criar("Maria", "maria@test.com");
        
        assertTrue(usuarioComCpf.temCpf());
        assertFalse(usuarioSemCpf.temCpf());
    }
}

