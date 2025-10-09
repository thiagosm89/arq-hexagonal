/**
 * Entidades do Domain
 * 
 * Entidades são objetos com identidade única que podem mudar de estado ao longo do tempo.
 * 
 * Diferença entre Entidade e Value Object:
 * - Entidade: Tem identidade (ID), pode mudar, igualdade por ID
 * - Value Object: Sem identidade, imutável, igualdade por valor
 * 
 * Exemplo:
 * - Usuario (Entidade): Tem ID, pode mudar nome, email, etc.
 * - Email (Value Object): Sem ID, imutável, igualdade por valor
 * 
 * Entidades podem CONTER Value Objects:
 * - Usuario tem Email (Value Object)
 * - Usuario tem CPF (Value Object)
 * 
 * @since 1.0
 */
package com.example.domain.model;

