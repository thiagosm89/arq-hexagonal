package com.example.application.config;

import com.example.domain.ports.in.UsuarioInboundPort;
import com.example.domain.ports.out.UsuarioOutboundPort;
import com.example.domain.usecase.UsuarioUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração Manual dos Beans da Lógica de Negócio
 * 
 * Esta classe mantém o Domain puro, sem dependências de frameworks.
 * Aqui criamos manualmente os beans que implementam as portas de entrada,
 * injetando as implementações das portas de saída.
 * 
 * Nomenclatura correta:
 * - InboundPort: porta de entrada (genérica, não indica implementação)
 * - OutboundPort: porta de saída (genérica, pode ser BD, REST, etc.)
 * 
 * Isso permite que a lógica de negócio fique no Domain,
 * mas a configuração Spring fique isolada no Application.
 */
@Configuration
public class UseCaseConfiguration {
    
    /**
     * Cria e configura o bean da porta de entrada de Usuário
     * 
     * @param usuarioOutboundPort implementação da porta de saída (vem da camada Infrastructure)
     * @return instância configurada da lógica de negócio
     */
    @Bean
    public UsuarioInboundPort usuarioInboundPort(UsuarioOutboundPort usuarioOutboundPort) {
        return new UsuarioUseCaseImpl(usuarioOutboundPort);
    }

}


