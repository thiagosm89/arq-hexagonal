package com.example.domain.usecase;

import com.example.domain.exception.UsuarioInvalidoException;
import com.example.domain.exception.UsuarioNaoEncontradoException;
import com.example.domain.model.Usuario;
import com.example.domain.ports.out.UsuarioOutboundPort;
import com.example.domain.valueobject.CPF;
import com.example.domain.valueobject.Email;
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
        Email email = Email.of("joao@example.com");
        CPF cpf = CPF.of("123.456.789-09");
        usuarioValido = new Usuario(1L, "João Silva", email, cpf);
    }
    
    @Test
    void deveCriarUsuarioComSucesso() {
        when(usuarioOutboundPort.buscarPorEmail(anyString())).thenReturn(Optional.empty());
        when(usuarioOutboundPort.salvar(any(Usuario.class))).thenReturn(usuarioValido);
        
        Email email = Email.of("joao@example.com");
        CPF cpf = CPF.of("123.456.789-09");
        
        Usuario resultado = usuarioUseCase.criarUsuario("João Silva", email, cpf);
        
        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        assertEquals(email, resultado.getEmail());
        verify(usuarioOutboundPort).salvar(any(Usuario.class));
    }
    
    @Test
    void deveLancarExcecaoAoCriarUsuarioComNomeInvalido() {
        Email email = Email.of("joao@example.com");
        
        assertThrows(UsuarioInvalidoException.class, () -> {
            usuarioUseCase.criarUsuario("", email, null);
        });
        
        verify(usuarioOutboundPort, never()).salvar(any(Usuario.class));
    }
    
    @Test
    void deveLancarExcecaoAoCriarUsuarioComEmailDuplicado() {
        when(usuarioOutboundPort.buscarPorEmail(anyString())).thenReturn(Optional.of(usuarioValido));
        
        Email email = Email.of("joao@example.com");
        
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
        // Cria usuários válidos com Value Objects
        Email email1 = Email.of("joao@example.com");
        CPF cpf1 = CPF.of("123.456.789-09");
        Usuario usuario1 = new Usuario(1L, "João Silva", email1, cpf1);
        
        Email email2 = Email.of("maria@example.com");
        CPF cpf2 = CPF.of("987.654.321-00");
        Usuario usuario2 = new Usuario(2L, "Maria Santos", email2, cpf2);
        
        List<Usuario> usuarios = Arrays.asList(usuario1, usuario2);
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
    
    @Test
    void deveCriarUsuarioSemCPF() {
        when(usuarioOutboundPort.buscarPorEmail(anyString())).thenReturn(Optional.empty());
        when(usuarioOutboundPort.salvar(any(Usuario.class))).thenReturn(usuarioValido);
        
        Email email = Email.of("joao@example.com");
        
        Usuario resultado = usuarioUseCase.criarUsuario("João Silva", email, null);
        
        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        assertEquals(email, resultado.getEmail());
        assertNull(resultado.getCpf());
        verify(usuarioOutboundPort).salvar(any(Usuario.class));
    }
    
    @Test
    void deveLancarExcecaoAoCriarUsuarioComEmailInvalido() {
        assertThrows(IllegalArgumentException.class, () -> {
            Email email = Email.of("email-invalido");
            usuarioUseCase.criarUsuario("João Silva", email, null);
        });
        
        verify(usuarioOutboundPort, never()).salvar(any(Usuario.class));
    }
    
    @Test
    void deveLancarExcecaoAoCriarUsuarioComCPFInvalido() {
        assertThrows(IllegalArgumentException.class, () -> {
            Email email = Email.of("joao@example.com");
            CPF cpf = CPF.of("123"); // CPF inválido
            usuarioUseCase.criarUsuario("João Silva", email, cpf);
        });
        
        verify(usuarioOutboundPort, never()).salvar(any(Usuario.class));
    }
}

