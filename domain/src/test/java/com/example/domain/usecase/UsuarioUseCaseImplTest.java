package com.example.domain.usecase;

import com.example.domain.exception.UsuarioInvalidoException;
import com.example.domain.exception.UsuarioNaoEncontradoException;
import com.example.domain.model.Usuario;
import com.example.domain.ports.out.UsuarioOutboundPort;
import com.example.domain.valueobject.CPF;
import com.example.domain.valueobject.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    private static final Email EMAIL = Email.of("joao@example.com");
    private static final CPF CPF_USER = CPF.of("123.456.789-09");

    
    @BeforeEach
    void setUp() {
        // Instanciação manual - SEM Spring
        usuarioUseCase = new UsuarioUseCaseImpl(usuarioOutboundPort);
        
        // Cria usuário válido com Value Objects

        usuarioValido = new Usuario(1L, "João Silva", EMAIL, CPF_USER);
    }

    @DisplayName("Teste de Criação de Usuário")
    @Nested
    public class CriarUsuario {

        @DisplayName("Validar argumentos do método")
        @Nested
        public class ValidacaoArgumentos {

            @Test
            void deveLancarExcecaoSeNomeNull() {
                NullPointerException exceptionResult = assertThrows(NullPointerException.class, () -> {
                    usuarioUseCase.criarUsuario(null, EMAIL, CPF_USER);
                });

                assertEquals("nome null", exceptionResult.getMessage());

                verify(usuarioOutboundPort, never()).buscarPorEmail(anyString());
                verify(usuarioOutboundPort, never()).salvar(any(Usuario.class));
            }

            @Test
            void deveLancarExcecaoSeEmailNull() {
                NullPointerException exceptionResult = assertThrows(NullPointerException.class, () -> {
                    usuarioUseCase.criarUsuario("Outro Usuário", null, CPF_USER);
                });

                assertEquals("email null", exceptionResult.getMessage());

                verify(usuarioOutboundPort, never()).buscarPorEmail(anyString());
                verify(usuarioOutboundPort, never()).salvar(any(Usuario.class));
            }

            @Test
            void deveLancarExcecaoSeCpfNull() {
                Email email = Email.of("joao@example.com");

                NullPointerException exceptionResult = assertThrows(NullPointerException.class, () -> {
                    usuarioUseCase.criarUsuario("Outro Usuário", email, null);
                });

                assertEquals("cpf null", exceptionResult.getMessage());

                verify(usuarioOutboundPort, never()).buscarPorEmail(anyString());
                verify(usuarioOutboundPort, never()).salvar(any(Usuario.class));
            }

            @Test
            void deveLancarExcecaoAoCriarUsuarioComNomeInvalido() {
                Email email = Email.of("joao@example.com");

                UsuarioInvalidoException exceptionResult = assertThrows(UsuarioInvalidoException.class, () -> {
                    usuarioUseCase.criarUsuario("", email, CPF_USER);
                });

                assertEquals("Dados do usuário são inválidos", exceptionResult.getMessage());

                verify(usuarioOutboundPort, never()).salvar(any(Usuario.class));
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
                    CPF cpf = CPF_USER.of("123"); // CPF inválido
                    usuarioUseCase.criarUsuario("João Silva", email, cpf);
                });

                verify(usuarioOutboundPort, never()).salvar(any(Usuario.class));
            }

        }

        @Test
        @DisplayName("Usuário criado com sucesso")
        void deveCriarUsuarioComSucesso() throws UsuarioInvalidoException {
            when(usuarioOutboundPort.buscarPorEmail(anyString())).thenReturn(Optional.empty());
            when(usuarioOutboundPort.salvar(any(Usuario.class))).thenReturn(usuarioValido);

            Email email = Email.of("joao@example.com");
            CPF cpf = CPF_USER.of("123.456.789-09");

            Usuario resultado = usuarioUseCase.criarUsuario("João Silva", email, cpf);

            assertNotNull(resultado);
            assertEquals("João Silva", resultado.getNome());
            assertEquals(email, resultado.getEmail());
            verify(usuarioOutboundPort).salvar(any(Usuario.class));
        }

        @Test
        void deveLancarExcecaoAoCriarUsuarioComEmailDuplicado() {
            when(usuarioOutboundPort.buscarPorEmail(anyString())).thenReturn(Optional.of(usuarioValido));

            Email email = Email.of("joao@example.com");

            UsuarioInvalidoException exceptionResult = assertThrows(UsuarioInvalidoException.class, () -> {
                usuarioUseCase.criarUsuario("Outro Usuário", email, CPF_USER);
            });

            assertEquals("Já existe um usuário com o email: joao@example.com", exceptionResult.getMessage());

            verify(usuarioOutboundPort, never()).salvar(any(Usuario.class));
        }

    }

    @DisplayName("Teste de Buscar por Id um Usuário")
    @Nested
    public class BuscarUsuarioPorId {

        @Test
        void deveBuscarUsuarioPorId() throws UsuarioNaoEncontradoException {
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

    }

    @DisplayName("Teste para Listar todos Usuários")
    @Nested
    public class ListarTodosUsuarios {

        @Test
        void deveListarTodosUsuarios() {
            // Cria usuários válidos com Value Objects
            Email email1 = Email.of("joao@example.com");
            CPF cpf1 = CPF_USER.of("123.456.789-09");
            Usuario usuario1 = new Usuario(1L, "João Silva", email1, cpf1);

            Email email2 = Email.of("maria@example.com");
            CPF cpf2 = CPF_USER.of("987.654.321-00");
            Usuario usuario2 = new Usuario(2L, "Maria Santos", email2, cpf2);

            List<Usuario> usuarios = Arrays.asList(usuario1, usuario2);
            when(usuarioOutboundPort.buscarTodos()).thenReturn(usuarios);

            List<Usuario> resultado = usuarioUseCase.listarTodosUsuarios();

            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            verify(usuarioOutboundPort).buscarTodos();
        }

    }

    @DisplayName("Teste para remover um usuário")
    @Nested
    public class RemoverUsuario {

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

}

