package com.example.domain.usecase;

import com.example.domain.exception.UsuarioInvalidoException;
import com.example.domain.exception.UsuarioNaoEncontradoException;
import com.example.domain.model.Usuario;
import com.example.domain.ports.out.UsuarioOutboundPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes para UsuarioUseCaseImpl - PUROS, sem dependências de Spring
 * Testa a lógica de negócio que implementa a InboundPort
 */
@ExtendWith(MockitoExtension.class)
class UsuarioUseCaseImplTest {
    
    @Mock
    private UsuarioOutboundPort usuarioOutboundPort;
    
    private UsuarioUseCaseImpl usuarioUseCase;
    
    private Usuario usuarioValido;
    
    @BeforeEach
    void setUp() {
        // Instanciação manual - SEM Spring
        usuarioUseCase = new UsuarioUseCaseImpl(usuarioOutboundPort);
        
        // Cria usuário válido com Value Objects
        com.example.domain.valueobject.Email email = 
            com.example.domain.valueobject.Email.of("joao@example.com");
        com.example.domain.valueobject.CPF cpf = 
            com.example.domain.valueobject.CPF.of("123.456.789-09");
        usuarioValido = new Usuario(1L, "João Silva", email, cpf);
    }
    
    @Test
    void deveCriarUsuarioComSucesso() {
        when(usuarioOutboundPort.buscarPorEmail(anyString())).thenReturn(Optional.empty());
        when(usuarioOutboundPort.salvar(any(Usuario.class))).thenReturn(usuarioValido);
        
        com.example.domain.valueobject.Email email = 
            com.example.domain.valueobject.Email.of("joao@example.com");
        com.example.domain.valueobject.CPF cpf = 
            com.example.domain.valueobject.CPF.of("123.456.789-09");
        
        Usuario resultado = usuarioUseCase.criarUsuario("João Silva", email, cpf);
        
        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        assertEquals(email, resultado.getEmail());
        verify(usuarioOutboundPort).salvar(any(Usuario.class));
    }
    
    @Test
    void deveLancarExcecaoAoCriarUsuarioComNomeInvalido() {
        com.example.domain.valueobject.Email email = 
            com.example.domain.valueobject.Email.of("joao@example.com");
        
        assertThrows(UsuarioInvalidoException.class, () -> {
            usuarioUseCase.criarUsuario("", email, null);
        });
        
        verify(usuarioOutboundPort, never()).salvar(any(Usuario.class));
    }
    
    @Test
    void deveLancarExcecaoAoCriarUsuarioComEmailDuplicado() {
        when(usuarioOutboundPort.buscarPorEmail(anyString())).thenReturn(Optional.of(usuarioValido));
        
        com.example.domain.valueobject.Email email = 
            com.example.domain.valueobject.Email.of("joao@example.com");
        
        assertThrows(UsuarioInvalidoException.class, () -> {
            usuarioUseCase.criarUsuario("Outro Usuário", email, null);
        });
        
        verify(usuarioOutboundPort, never()).salvar(any(Usuario.class));
    }
    
    @Test
    void deveBuscarUsuarioPorId() {
        when(usuarioOutboundPort.buscarPorId(1L)).thenReturn(Optional.of(usuarioValido));
        
        Usuario resultado = usuarioUseCase.buscarUsuarioPorId(1L);
        
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(usuarioOutboundPort).buscarPorId(1L);
    }
    
    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(usuarioOutboundPort.buscarPorId(1L)).thenReturn(Optional.empty());
        
        assertThrows(UsuarioNaoEncontradoException.class, () -> {
            usuarioUseCase.buscarUsuarioPorId(1L);
        });
    }
    
    @Test
    void deveListarTodosUsuarios() {
        List<Usuario> usuarios = Arrays.asList(
            new Usuario(1L, "João Silva", "joao@example.com"),
            new Usuario(2L, "Maria Santos", "maria@example.com")
        );
        when(usuarioOutboundPort.buscarTodos()).thenReturn(usuarios);
        
        List<Usuario> resultado = usuarioUseCase.listarTodosUsuarios();
        
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(usuarioOutboundPort).buscarTodos();
    }
    
    @Test
    void deveRemoverUsuarioComSucesso() {
        when(usuarioOutboundPort.buscarPorId(1L)).thenReturn(Optional.of(usuarioValido));
        doNothing().when(usuarioOutboundPort).deletar(1L);
        
        assertDoesNotThrow(() -> usuarioUseCase.removerUsuario(1L));
        
        verify(usuarioOutboundPort).buscarPorId(1L);
        verify(usuarioOutboundPort).deletar(1L);
    }
    
    @Test
    void deveLancarExcecaoAoRemoverUsuarioInexistente() {
        when(usuarioOutboundPort.buscarPorId(1L)).thenReturn(Optional.empty());
        
        assertThrows(UsuarioNaoEncontradoException.class, () -> {
            usuarioUseCase.removerUsuario(1L);
        });
        
        verify(usuarioOutboundPort, never()).deletar(anyLong());
    }
}

