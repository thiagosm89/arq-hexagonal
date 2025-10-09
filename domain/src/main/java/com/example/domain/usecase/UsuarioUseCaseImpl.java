package com.example.domain.usecase;

import com.example.domain.exception.UsuarioInvalidoException;
import com.example.domain.exception.UsuarioNaoEncontradoException;
import com.example.domain.model.Usuario;
import com.example.domain.ports.in.UsuarioInboundPort;
import com.example.domain.ports.out.UsuarioOutboundPort;

import java.util.List;

/**
 * Implementação da lógica de negócio de Usuário
 * 
 * Implementa a porta de entrada (InboundPort) e usa a porta de saída (OutboundPort)
 * Contém a lógica de negócio pura e orquestra as operações
 * 
 * PURA - Sem dependências de frameworks
 */
public class UsuarioUseCaseImpl implements UsuarioInboundPort {
    
    private final UsuarioOutboundPort usuarioOutboundPort;
    
    public UsuarioUseCaseImpl(UsuarioOutboundPort usuarioOutboundPort) {
        this.usuarioOutboundPort = usuarioOutboundPort;
    }
    
    @Override
    public Usuario criarUsuario(String nome, Email email, CPF cpf) {
        // Email e CPF já vêm validados (Value Objects)!
        // Application fez a conversão e validação
        
        // Cria o usuário com Value Objects já validados
        Usuario usuario = new Usuario(nome, email, cpf);
        
        // Validação de negócio (regras do domínio)
        if (!usuario.isValid()) {
            throw new UsuarioInvalidoException("Dados do usuário são inválidos");
        }
        
        // Verifica se já existe um usuário com o mesmo email
        // Usa o método getValue() para comparar com string do banco
        usuarioOutboundPort.buscarPorEmail(email.getValue()).ifPresent(u -> {
            throw new UsuarioInvalidoException("Já existe um usuário com o email: " + email.getValue());
        });
        
        // Salva o usuário
        return usuarioOutboundPort.salvar(usuario);
    }
    
    @Override
    public Usuario buscarUsuarioPorId(Long id) {
        return usuarioOutboundPort.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
    }
    
    @Override
    public List<Usuario> listarTodosUsuarios() {
        return usuarioOutboundPort.buscarTodos();
    }
    
    @Override
    public void removerUsuario(Long id) {
        // Verifica se o usuário existe antes de remover
        buscarUsuarioPorId(id);
        usuarioOutboundPort.deletar(id);
    }
}

