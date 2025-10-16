package com.example.application;

import com.example.domain.model.Usuario;
import com.example.domain.ports.in.UsuarioInboundPort;
import com.example.domain.valueobject.CPF;
import com.example.domain.valueobject.Email;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Teste de Integração da Aplicação
 * Testa o fluxo completo passando por todos os módulos
 */
@SpringBootTest
@ActiveProfiles("test")
class ApplicationIntegrationTest {

    @Autowired
    private UsuarioInboundPort usuarioInboundPort;

    @Test
    void contextLoads() {
        assertNotNull(usuarioInboundPort);
    }

    @Test
    void deveCriarEBuscarUsuarioComSucesso() {
        // Criar usuário
        Usuario usuarioCriado = usuarioInboundPort.criarUsuario(
                "Teste Integration",
                Email.of("teste.integration@example.com"),
                CPF.of("01994445017")
        );

        assertNotNull(usuarioCriado);
        assertNotNull(usuarioCriado.getId());
        assertEquals("Teste Integration", usuarioCriado.getNome());
        assertEquals("teste.integration@example.com", usuarioCriado.getEmail());

        // Buscar usuário criado
        Usuario usuarioBuscado = usuarioInboundPort.buscarUsuarioPorId(usuarioCriado.getId());

        assertNotNull(usuarioBuscado);
        assertEquals(usuarioCriado.getId(), usuarioBuscado.getId());
        assertEquals(usuarioCriado.getNome(), usuarioBuscado.getNome());
        assertEquals(usuarioCriado.getEmail(), usuarioBuscado.getEmail());
    }

    @Test
    void deveListarTodosUsuarios() {
        // Criar alguns usuários
        usuarioInboundPort.criarUsuario("Usuario 1", Email.of("usuario1@test.com"), CPF.of("01994445017"));
        usuarioInboundPort.criarUsuario("Usuario 2", Email.of("usuario2@test.com"), CPF.of("00011122233"));

        // Listar todos
        List<Usuario> usuarios = usuarioInboundPort.listarTodosUsuarios();

        assertNotNull(usuarios);
        assertTrue(usuarios.size() >= 2);
    }

    @Test
    void deveRemoverUsuarioComSucesso() {
        // Criar usuário
        Usuario usuario = usuarioInboundPort.criarUsuario(
                "Usuario Para Remover",
                Email.of("remover@test.com"),
                CPF.of("01994445017")
        );

        Long idUsuario = usuario.getId();
        assertNotNull(idUsuario);

        // Remover usuário
        assertDoesNotThrow(() -> usuarioInboundPort.removerUsuario(idUsuario));
    }
}

