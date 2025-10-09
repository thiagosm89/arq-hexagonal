package com.example.application;

import com.example.domain.model.Usuario;
import com.example.domain.ports.in.UsuarioUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de Integração da Aplicação
 * Testa o fluxo completo passando por todos os módulos
 */
@SpringBootTest
@ActiveProfiles("test")
class ApplicationIntegrationTest {
    
    @Autowired
    private UsuarioUseCase usuarioUseCase;
    
    @Test
    void contextLoads() {
        assertNotNull(usuarioUseCase);
    }
    
    @Test
    void deveCriarEBuscarUsuarioComSucesso() {
        // Criar usuário
        Usuario usuarioCriado = usuarioUseCase.criarUsuario(
            "Teste Integration", 
            "teste.integration@example.com"
        );
        
        assertNotNull(usuarioCriado);
        assertNotNull(usuarioCriado.getId());
        assertEquals("Teste Integration", usuarioCriado.getNome());
        assertEquals("teste.integration@example.com", usuarioCriado.getEmail());
        
        // Buscar usuário criado
        Usuario usuarioBuscado = usuarioUseCase.buscarUsuarioPorId(usuarioCriado.getId());
        
        assertNotNull(usuarioBuscado);
        assertEquals(usuarioCriado.getId(), usuarioBuscado.getId());
        assertEquals(usuarioCriado.getNome(), usuarioBuscado.getNome());
        assertEquals(usuarioCriado.getEmail(), usuarioBuscado.getEmail());
    }
    
    @Test
    void deveListarTodosUsuarios() {
        // Criar alguns usuários
        usuarioUseCase.criarUsuario("Usuario 1", "usuario1@test.com");
        usuarioUseCase.criarUsuario("Usuario 2", "usuario2@test.com");
        
        // Listar todos
        List<Usuario> usuarios = usuarioUseCase.listarTodosUsuarios();
        
        assertNotNull(usuarios);
        assertTrue(usuarios.size() >= 2);
    }
    
    @Test
    void deveRemoverUsuarioComSucesso() {
        // Criar usuário
        Usuario usuario = usuarioUseCase.criarUsuario(
            "Usuario Para Remover", 
            "remover@test.com"
        );
        
        Long idUsuario = usuario.getId();
        assertNotNull(idUsuario);
        
        // Remover usuário
        assertDoesNotThrow(() -> usuarioUseCase.removerUsuario(idUsuario));
    }
}

