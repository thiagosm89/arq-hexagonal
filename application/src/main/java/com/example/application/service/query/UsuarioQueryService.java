package com.example.application.service.query;

import com.example.application.service.query.dto.UsuarioListResponse;
import com.example.application.rest.dto.UsuarioResponse;
import com.example.infrastructure.persistence.entity.UsuarioEntity;
import com.example.infrastructure.persistence.repository.UsuarioJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Query Service - Operações de LEITURA (Read)
 * 
 * Padrão CQRS: Separação de Commands e Queries
 * 
 * Este service implementa queries que fazem BYPASS do Domain.
 * 
 * BYPASS = Otimização adicional (não faz parte do CQRS)
 * Queries simples PODEM pular Domain quando:
 * - Não há lógica de negócio
 * - São apenas buscas/listagens
 * - Performance é importante
 * 
 * ⚠️ CUIDADO: Se a query tiver lógica ou regras, DEVE passar por Domain!
 * 
 * Estrutura:
 * - CQRS: Separa Command/Query (organização)
 * - Bypass: Queries pulam Domain (performance)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioQueryService {
    
    private final UsuarioJpaRepository usuarioJpaRepository;
    
    /**
     * Lista todos os usuários
     * BYPASS: Vai direto ao repositório JPA (pula Domain)
     * 
     * Justificativa: Query simples sem lógica de negócio
     */
    public List<UsuarioListResponse> listarTodosUsuarios() {
        log.info("Query: Listar todos os usuários (BYPASS Domain)");
        
        // Vai DIRETO na Infrastructure (JPA Repository)
        return usuarioJpaRepository.findAll()
            .stream()
            .map(this::toListResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Busca usuário por ID
     * BYPASS: Vai direto ao repositório JPA
     * 
     * Justificativa: Busca simples sem lógica
     */
    public Optional<UsuarioResponse> buscarPorId(Long id) {
        log.info("Query: Buscar usuário por id {} (BYPASS Domain)", id);
        
        // Vai DIRETO na Infrastructure
        return usuarioJpaRepository.findById(id)
            .map(this::toResponse);
    }
    
    /**
     * Busca usuário por email
     * BYPASS: Vai direto ao repositório JPA
     * 
     * Justificativa: Query simples
     */
    public Optional<UsuarioResponse> buscarPorEmail(String email) {
        log.info("Query: Buscar usuário por email {} (BYPASS Domain)", email);
        
        // Vai DIRETO na Infrastructure
        return usuarioJpaRepository.findByEmail(email)
            .map(this::toResponse);
    }
    
    /**
     * Conta total de usuários
     * BYPASS: Estatística simples
     */
    public Long contarUsuarios() {
        log.info("Query: Contar usuários (BYPASS Domain)");
        
        return usuarioJpaRepository.count();
    }
    
    /**
     * Exemplo de query que NÃO deveria fazer bypass:
     * "Buscar usuários ativos que podem receber notificações"
     * 
     * Esta query TEM lógica de negócio (o que é "ativo"? "pode receber"?)
     * Então deveria ir por Domain!
     */
    // public List<UsuarioResponse> buscarUsuariosNotificaveis() {
    //     // NÃO FAZER: isso tem regra de negócio!
    //     // FAZER: chamar um Use Case no Domain
    // }
    
    private UsuarioResponse toResponse(UsuarioEntity entity) {
        return new UsuarioResponse(
            entity.getId(),
            entity.getNome(),
            entity.getEmail()
        );
    }
    
    private UsuarioListResponse toListResponse(UsuarioEntity entity) {
        return new UsuarioListResponse(
            entity.getId(),
            entity.getNome(),
            entity.getEmail()
        );
    }
}

