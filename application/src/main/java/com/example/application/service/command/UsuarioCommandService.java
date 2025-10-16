package com.example.application.service.command;

import com.example.application.exception.ApiException;
import com.example.application.exception.ResponseErrorCode;
import com.example.domain.exception.UsuarioInvalidoException;
import com.example.domain.exception.UsuarioNaoEncontradoException;
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
            Email email = Email.of(request.getEmail());
            CPF cpf = CPF.of(request.getCpf());

            Usuario usuario = usuarioInboundPort.criarUsuario(
                    request.getNome(),
                    email,  // Value Object
                    cpf     // Value Object (null se não informado)
            );

            return toResponse(usuario);
        } catch (IllegalArgumentException | UsuarioInvalidoException ex) {
            throw new ApiException(ResponseErrorCode.INVALID_REQUEST, ex.getMessage(), ex);
        }

    }

    public void removerUsuario(Long id) {
        try {
            usuarioInboundPort.removerUsuario(id);
        } catch (UsuarioNaoEncontradoException ex) {
            throw new ApiException(ResponseErrorCode.NOT_FOUND, ex.getMessage(), ex);
        }
    }

    /**
     * TODO: construir na hora com o pessoal para explicar o Exception tratável
     */
    public UsuarioResponse buscarUsuarioPorId(Long id) {
        /*try {
            Usuario usuario = usuarioInboundPort.buscarUsuarioPorId(id);
            return toResponse(usuario);
        } catch (UsuarioNaoEncontradoException ex) {
            throw new ApiException(ResponseErrorCode.USUARIO_NAO_ENCONTRADO, "Usuário não encontrado ao tentar atualizar.", ex);
        }*/
        return null;
    }
    
    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmailAsString(),
            usuario.getCpfAsString()
        );
    }
}

