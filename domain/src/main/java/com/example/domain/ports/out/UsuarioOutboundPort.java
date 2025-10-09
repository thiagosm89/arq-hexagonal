package com.example.domain.ports.out;

import com.example.domain.model.Usuario;
import java.util.List;
import java.util.Optional;

/**
 * Porta de Saída (Outbound Port) - Define operações que o Domain precisa de infraestrutura
 * 
 * Nome genérico e agnóstico de tecnologia:
 * - Não é "Repository" (isso indicaria JPA/Database)
 * - É "OutboundPort" (pode ser: BD, REST client, mensageria, cache, arquivo, etc.)
 * 
 * Esta interface será implementada pela camada Infrastructure
 * e chamada pela camada Domain
 * 
 * Exemplos de possíveis implementações:
 * - UsuarioJpaAdapter (banco de dados)
 * - UsuarioRestClientAdapter (API externa)
 * - UsuarioKafkaAdapter (mensageria)
 * - UsuarioCacheAdapter (Redis)
 */
public interface UsuarioOutboundPort {
    
    /**
     * Persiste um usuário
     * @param usuario Usuário a ser persistido
     * @return Usuário persistido
     */
    Usuario salvar(Usuario usuario);
    
    /**
     * Busca um usuário por ID
     * @param id ID do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<Usuario> buscarPorId(Long id);
    
    /**
     * Busca todos os usuários
     * @return Lista de usuários
     */
    List<Usuario> buscarTodos();
    
    /**
     * Remove um usuário
     * @param id ID do usuário a ser removido
     */
    void deletar(Long id);
    
    /**
     * Busca um usuário por email
     * @param email Email do usuário (string para compatibilidade com banco)
     * @return Optional contendo o usuário se encontrado
     */
    Optional<Usuario> buscarPorEmail(String email);
}
