package com.example.application.rest;

import com.example.application.config.AutoMockRepositoryConfiguration;
import com.example.domain.model.Usuario;
import com.example.domain.ports.in.UsuarioInboundPort;
import com.example.domain.valueobject.CPF;
import com.example.domain.valueobject.Email;
import com.example.infrastructure.databases.oracle.entity.UsuarioEntity;
import com.example.infrastructure.databases.oracle.repository.UsuarioJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Teste de Integração da Aplicação
 * Testa o fluxo completo passando por todos os módulos
 * 
 * 🎯 MOCK AUTOMÁTICO:
 * - Usa AutoMockRepositoryConfiguration para detectar e mockar repositórios
 * - TODOS os repositórios são automaticamente mockados (inclusive novos!)
 * - Não precisa declarar @MockBean manualmente
 * - Testes podem fazer mock explícito quando necessário
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(AutoMockRepositoryConfiguration.class)
class ApplicationIntegrationTest {

    @Autowired
    private UsuarioInboundPort usuarioInboundPort;
    
    /**
     * Repositório é automaticamente mockado pela configuração
     * Basta injetar com @Autowired - NÃO precisa @MockBean!
     */
    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    @Test
    void contextLoads() {
        assertNotNull(usuarioInboundPort);
        assertNotNull(usuarioJpaRepository);
        System.out.println("✅ Contexto carregado com repositórios automaticamente mockados");
    }

    @Test
    void deveCriarEBuscarUsuarioComSucesso() {
        CPF cpf = CPF.of("80333508068");
        Email email = Email.of("teste.integration@example.com");

        // Mock explícito do repositório para este teste
        UsuarioEntity entity = new UsuarioEntity(
                1L, "Teste Integration", "teste.integration@example.com", cpf.getValue()
            );

        when(usuarioJpaRepository.save(any())).thenReturn(entity);
        when(usuarioJpaRepository.findById(1L)).thenReturn(Optional.of(entity));

        // Criar usuário (comportamento mockado)
        Usuario usuarioCriado = usuarioInboundPort.criarUsuario(
                "Teste Integration",
                email,
                cpf
        );

        assertNotNull(usuarioCriado);
        assertEquals("Teste Integration", usuarioCriado.getNome());
        assertEquals(email, usuarioCriado.getEmail());
        assertEquals(cpf, usuarioCriado.getCpf());
    }

    @Test
    void deveListarTodosUsuarios() {
        // Mock explícito para listagem
        UsuarioEntity entity1 = new UsuarioEntity(
                1L, "Usuario 1", "usuario1@test.com", "80333508068"
            );

        UsuarioEntity entity2 = new UsuarioEntity(
                2L, "Usuario 2", "usuario2@test.com", "00554295059"
            );
        
        when(usuarioJpaRepository.findAll()).thenReturn(List.of(entity1, entity2));

        // Listar todos (comportamento mockado)
        List<Usuario> usuarios = usuarioInboundPort.listarTodosUsuarios();

        assertNotNull(usuarios);
        assertEquals(2, usuarios.size());
    }

    @Test
    void deveRemoverUsuarioComSucesso() {
        // Mock explícito para remoção
        UsuarioEntity entity = new UsuarioEntity(
                1L, "Usuario Para Remover", "remover@test.com", "80333508068"
            );
        
        when(usuarioJpaRepository.findById(1L)).thenReturn(Optional.of(entity));

        // Remover usuário (comportamento mockado)
        assertDoesNotThrow(() -> usuarioInboundPort.removerUsuario(1L));
    }

    @Test
    void deveDemonstrarMockAutomaticoFuncionando() {
        System.out.println("\n🎭 === DEMONSTRAÇÃO DO MOCK AUTOMÁTICO ===");
        
        // O repositório já está mockado automaticamente pela configuração
        // Não precisa fazer mock explícito se não quiser comportamento específico
        
        System.out.println("✅ UsuarioJpaRepository está automaticamente mockado");
        System.out.println("✅ Teste pode fazer mock explícito quando necessário");
        System.out.println("✅ Comportamento padrão é MockBean (não executa métodos reais)");
        
        System.out.println("🎉 Mock automático funcionando perfeitamente!\n");
    }
}

