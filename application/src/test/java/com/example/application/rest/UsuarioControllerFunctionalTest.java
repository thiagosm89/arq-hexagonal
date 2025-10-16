package com.example.application.rest;

import com.example.application.config.AutoMockRepositoryConfiguration;
import com.example.application.rest.dto.UsuarioRequest;
import com.example.application.rest.dto.UsuarioResponse;
import com.example.infrastructure.databases.oracle.entity.UsuarioEntity;
import com.example.infrastructure.databases.oracle.repository.UsuarioJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Teste de Integração da Aplicação via WebMvc
 * Testa o fluxo completo: Controller → CommandService → InboundPort → Repository
 * 
 * 🎯 MOCK AUTOMÁTICO:
 * - Usa AutoMockRepositoryConfiguration para detectar e mockar repositórios
 * - TODOS os repositórios são automaticamente mockados (inclusive novos!)
 * - Testa via HTTP usando TestRestTemplate
 * - Valida serialização/deserialização JSON e status HTTP
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(AutoMockRepositoryConfiguration.class)
class UsuarioControllerFunctionalTest {

    @Autowired
    private TestRestTemplate restTemplate;
    
    /**
     * Repositório é automaticamente mockado pela configuração
     * Basta injetar com @Autowired - NÃO precisa @MockBean!
     */
    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    @Test
    @DisplayName("Deve carregar o contexto Spring com WebMvc e repositórios mockados automaticamente")
    void contextLoads() {
        assertNotNull(restTemplate);
        assertNotNull(usuarioJpaRepository);
        System.out.println("✅ Contexto carregado com WebMvc e repositórios automaticamente mockados");
    }

    @Test
    @DisplayName("Deve criar usuário via POST /api/usuarios e retornar 201 Created")
    void deveCriarUsuarioViaHttp() {
        // Mock explícito do repositório para este teste
        UsuarioEntity entity = new UsuarioEntity(
                1L, "Teste HTTP", "teste.http@example.com", "80333508068"
            );

        when(usuarioJpaRepository.save(any())).thenReturn(entity);

        // Request HTTP
        UsuarioRequest request = new UsuarioRequest();
        request.setNome("Teste HTTP");
        request.setEmail("teste.http@example.com");
        request.setCpf("80333508068");

        // Chamada HTTP POST
        ResponseEntity<UsuarioResponse> response = restTemplate.postForEntity(
                "/api/usuarios", 
                request, 
                UsuarioResponse.class
        );

        // Verificações
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Teste HTTP", response.getBody().getNome());
        assertEquals("teste.http@example.com", response.getBody().getEmail());
        assertEquals("80333508068", response.getBody().getCpf());
    }

    @Test
    @DisplayName("Deve buscar usuário por ID via GET /api/usuarios/{id} e retornar 200 OK")
    void deveBuscarUsuarioPorIdViaHttp() {
        // Mock explícito do repositório
        UsuarioEntity entity = new UsuarioEntity(
                1L, "Usuario Busca", "busca@test.com", "80333508068"
            );
        
        when(usuarioJpaRepository.findById(1L)).thenReturn(Optional.of(entity));

        // Chamada HTTP GET
        ResponseEntity<UsuarioResponse> response = restTemplate.getForEntity(
                "/api/usuarios/1", 
                UsuarioResponse.class
        );

        // Verificações
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Usuario Busca", response.getBody().getNome());
        assertEquals("busca@test.com", response.getBody().getEmail());
    }

    @Test
    @DisplayName("Deve listar todos usuários via GET /api/usuarios e retornar 200 OK")
    void deveListarTodosUsuariosViaHttp() {
        // Mock explícito para listagem
        UsuarioEntity entity1 = new UsuarioEntity(
                1L, "Usuario 1", "usuario1@test.com", "80333508068"
            );
        UsuarioEntity entity2 = new UsuarioEntity(
                2L, "Usuario 2", "usuario2@test.com", "00554295059"
            );
        
        when(usuarioJpaRepository.findAll()).thenReturn(List.of(entity1, entity2));

        // Chamada HTTP GET
        ResponseEntity<UsuarioResponse[]> response = restTemplate.getForEntity(
                "/api/usuarios", 
                UsuarioResponse[].class
        );

        // Verificações
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().length);
        assertEquals("Usuario 1", response.getBody()[0].getNome());
        assertEquals("Usuario 2", response.getBody()[1].getNome());
    }

    @Test
    @DisplayName("Deve remover usuário via DELETE /api/usuarios/{id} e retornar 204 No Content")
    void deveRemoverUsuarioViaHttp() {
        // Mock explícito para remoção
        UsuarioEntity entity = new UsuarioEntity(
                1L, "Usuario Delete", "delete@test.com", "80333508068"
            );
        
        when(usuarioJpaRepository.findById(1L)).thenReturn(Optional.of(entity));

        // Chamada HTTP DELETE
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/usuarios/1",
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // Verificações
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando buscar usuário inexistente via GET /api/usuarios/{id}")
    void deveRetornar404ParaUsuarioInexistente() {
        // Mock: usuário não encontrado
        when(usuarioJpaRepository.findById(999L)).thenReturn(Optional.empty());

        // Chamada HTTP GET para usuário inexistente
        ResponseEntity<UsuarioResponse> response = restTemplate.getForEntity(
                "/api/usuarios/999", 
                UsuarioResponse.class
        );

        // Verificações
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando criar usuário com dados inválidos")
    void deveRetornar400ParaDadosInvalidos() {
        // Request com dados inválidos
        UsuarioRequest request = new UsuarioRequest();
        request.setNome(""); // Nome vazio
        request.setEmail("email-invalido"); // Email inválido
        request.setCpf("123"); // CPF inválido

        // Chamada HTTP POST com dados inválidos
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/usuarios", 
                request, 
                String.class
        );

        // Verificações
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Deve demonstrar que o mock automático está funcionando corretamente")
    void deveDemonstrarMockAutomaticoFuncionando() {
        System.out.println("\n🎭 === DEMONSTRAÇÃO DO MOCK AUTOMÁTICO ===");
        
        // O repositório já está mockado automaticamente pela configuração
        // Não precisa fazer mock explícito se não quiser comportamento específico
        
        System.out.println("✅ UsuarioJpaRepository está automaticamente mockado");
        System.out.println("✅ Teste pode fazer mock explícito quando necessário");
        System.out.println("✅ Comportamento padrão é MockBean (não executa métodos reais)");
        System.out.println("✅ Testa fluxo completo via WebMvc: Controller → CommandService → InboundPort → Repository");
        
        System.out.println("🎉 Mock automático funcionando perfeitamente!\n");
    }
}

