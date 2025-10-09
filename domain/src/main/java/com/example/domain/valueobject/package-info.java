/**
 * Value Objects do Domain
 * 
 * Value Objects são objetos imutáveis que representam conceitos do domínio
 * definidos pelo seu valor, não por identidade.
 * 
 * Características:
 * - Imutáveis (final, sem setters)
 * - Auto-validáveis (validação no construtor)
 * - Equals/hashCode baseados no valor
 * - Substituem tipos primitivos com regras de negócio
 * 
 * Exemplos neste pacote:
 * - Email: encapsula validação de email
 * - CPF: encapsula validação de CPF brasileiro
 * 
 * Benefícios:
 * - Validação centralizada
 * - Impossível ter valor inválido
 * - Código mais expressivo
 * - Type safety (não confunde String com Email)
 * 
 * @since 1.0
 */
package com.example.domain.valueobject;

