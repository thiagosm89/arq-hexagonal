package com.example.infrastructure.databases.oracle.repository;

import com.example.infrastructure.databases.oracle.entity.PessoaEntity;
import com.example.infrastructure.databases.oracle.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório JPA do Spring Data
 */
@Repository
public interface PessoaJpaRepository extends JpaRepository<PessoaEntity, Long> {
    Optional<UsuarioEntity> findByEmail(String email);
}

