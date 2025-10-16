package com.example.infrastructure.adapter;

import com.example.domain.model.Usuario;
import com.example.domain.ports.out.UsuarioOutboundPort;
import com.example.infrastructure.databases.oracle.entity.UsuarioEntity;
import com.example.infrastructure.databases.oracle.repository.UsuarioJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de Saída (Driven Adapter) - Implementa a porta de saída usando JPA
 * 
 * Implementa UsuarioOutboundPort (interface genérica do Domain)
 * usando tecnologia específica (JPA/Database)
 * 
 * Este adapter converte entre objetos de Domínio e Entidades JPA
 */
@Component
@RequiredArgsConstructor
public class UsuarioRepositoryAdapter implements UsuarioOutboundPort {
    
    private final UsuarioJpaRepository jpaRepository;
    
    @Override
    public Usuario salvar(Usuario usuario) {
        UsuarioEntity entity = toEntity(usuario);
        UsuarioEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }
    
    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }
    
    @Override
    public List<Usuario> buscarTodos() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deletar(Long id) {
        jpaRepository.deleteById(id);
    }
    
    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(this::toDomain);
    }
    
    // Métodos de conversão entre Domain e Infrastructure
    // Domain usa Value Objects (Email, CPF)
    // Infrastructure/JPA usa Strings no banco
    
    private UsuarioEntity toEntity(Usuario usuario) {
        return new UsuarioEntity(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmailAsString(),  // Value Object → String
                usuario.getCpfAsString()     // Value Object → String (pode ser null)
        );
    }
    
    private Usuario toDomain(UsuarioEntity entity) {
        // Converte de volta: Strings → Value Objects
        Usuario usuario = Usuario.criar(
                entity.getNome(),
                entity.getEmail(),  // String → Email (Value Object)
                entity.getCpf()     // String → CPF (Value Object)
        );
        // Usa reflection ou cria um novo construtor com ID
        return new Usuario(
                entity.getId(),
                entity.getNome(),
                usuario.getEmail(),
                usuario.getCpf()
        );
    }
}

