package com.example.application.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração dos Beans da Aplicação
 * Configura o scan de componentes de todos os módulos
 */
@Configuration
@ComponentScan(basePackages = {
    "com.example.infrastructure",
    "com.example.application"
})
public class BeanConfiguration {
    // Configurações adicionais podem ser adicionadas aqui
}

