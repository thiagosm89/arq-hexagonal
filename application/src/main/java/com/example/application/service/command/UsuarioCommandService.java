package com.example.application.service.command;

import com.example.domain.model.Usuario;
import com.example.domain.ports.in.UsuarioInboundPort;
import com.example.domain.valueobject.CPF;
import com.example.domain.valueobject.Email;
import com.example.application.rest.dto.UsuarioRequest;
import com.example.application.rest.dto.UsuarioResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Command Service - Operações de ESCRITA (Write)
 * 
 * Padrão CQRS: Separação de Commands e Queries
 * 
 * Commands (POST, PUT, DELETE) SEMPRE passam pela camada de Domain
 * porque envolvem mudanças de estado e lógica de negócio.
 * 
 * Usa a InboundPort (porta de entrada) do Domain
 * 
 * ⚠️ @Transactional deve ficar AQUI (fronteira transacional)
 * NÃO no Controller (camada HTTP) e NÃO no Domain (deve ser puro)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioCommandService {
    
    private final UsuarioInboundPort usuarioInboundPort;
    
    /**
     * Cria um novo usuário
     * PASSA por Domain: validação, verificação de email duplicado, regras de negócio
     * 
     * Application é responsável por:
     * - Receber dados do adapter (REST, CLI, etc.)
     * - Converter Strings → Value Objects
     * - Chamar o Domain com objetos válidos
     */
    public UsuarioResponse criarUsuario(UsuarioRequest request) {
        log.info("Command: Criar usuário com email {}", request.getEmail());
        
        try {
            // Application converte Strings → Value Objects do Domain
            Email email = Email.of(request.getEmail());
            
            // CPF é opcional
            CPF cpf = null;
            if (request.getCpf() != null && !request.getCpf().isBlank()) {
                cpf = CPF.of(request.getCpf());
            }
            
            // Chama a porta de entrada com Value Objects já validados
            Usuario usuario = usuarioInboundPort.criarUsuario(
                request.getNome(),
                email,  // Value Object
                cpf     // Value Object (null se não informado)
            );
            
            return toResponse(usuario);
            
        } catch (IllegalArgumentException e) {
            // Value Object lançou exceção na validação
            log.error("Erro de validação: {}", e.getMessage());
            throw new RuntimeException("Dados inválidos: " + e.getMessage(), e);
        }
    }
    
    /**
     * Remove um usuário
     * PASSA por Domain: verificação de existência, possíveis validações de deleção
     */
    public void removerUsuario(Long id) {
        log.info("Command: Remover usuário com id {}", id);
        
        // Chama a porta de entrada (InboundPort) do Domain
        usuarioInboundPort.removerUsuario(id);
    }
    
    /**
     * Atualiza um usuário (exemplo adicional)
     * PASSA por Domain: validações, regras de negócio
     */
    public UsuarioResponse atualizarUsuario(Long id, UsuarioRequest request) {
        log.info("Command: Atualizar usuário com id {}", id);
        
        // Aqui você implementaria um método de atualização na InboundPort
        // usuarioInboundPort.atualizarUsuario(id, nome, email);
        
        // Por enquanto, vamos buscar e retornar
        Usuario usuario = usuarioInboundPort.buscarUsuarioPorId(id);
        return toResponse(usuario);
    }
    
    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmailAsString(),
            usuario.getCpfAsString(),
            usuario.temCpf() ? usuario.getCpf().getFormatted() : null
        );
    }
}

