package com.example.domain.ports.in;

import com.example.domain.exception.UsuarioInvalidoException;
import com.example.domain.exception.UsuarioNaoEncontradoException;
import com.example.domain.model.Usuario;
import com.example.domain.valueobject.CPF;
import com.example.domain.valueobject.Email;
import java.util.List;

/**
 * Porta de Entrada (Inbound Port) - Define as operações que a aplicação oferece
 * 
 * Nome genérico e agnóstico de implementação:
 * - Não é "UseCase" (isso seria uma implementação específica)
 * - É "InboundPort" (qualquer tipo de entrada: REST, CLI, GraphQL, etc.)
 * 
 * Esta interface será implementada pela camada Domain (lógica de negócio)
 * e chamada pela camada Application (adaptadores de entrada)
 * 
 * ✅ BOA PRÁTICA: Usa Value Objects como parâmetros
 * - Application conhece Domain e pode criar Value Objects
 * - Type safety: impossível passar tipos errados
 * - Validação mais cedo: falha no Application, não no UseCase
 * - Port mais expressivo: fica claro que espera Email, não String
 */
public interface UsuarioInboundPort {
    
    /**
     * Cria um novo usuário no sistema
     * 
     * @param nome Nome do usuário
     * @param email Email do usuário
     * @param cpf CPF do usuário
     * @return Usuário criado
     *
     * @throws UsuarioInvalidoException Se os dados forem inválidos
     * @throws NullPointerException Se algum dos parâmetros for passado como null
     */
    Usuario criarUsuario(String nome, Email email, CPF cpf) throws UsuarioInvalidoException;
    
    /**
     * Busca um usuário por ID
     * @param id ID do usuário
     * @return Usuário encontrado
     */
    Usuario buscarUsuarioPorId(Long id) throws UsuarioNaoEncontradoException;
    
    /**
     * Lista todos os usuários
     * @return Lista de usuários
     */
    List<Usuario> listarTodosUsuarios();
    
    /**
     * Remove um usuário
     * @param id ID do usuário a ser removido
     */
    void removerUsuario(Long id) throws UsuarioNaoEncontradoException;
}

