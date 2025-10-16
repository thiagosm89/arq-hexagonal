package com.example.application.config;

import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.stereotype.Repository;

/**
 * Configuração Automática para Mock de Repositórios
 * 
 * 🎯 OBJETIVO: 
 * - Detectar automaticamente TODAS as classes @Repository
 * - Substituir todas por Mocks sem configuração manual
 * - Funciona para novos repositórios automaticamente
 * 
 * ⚡ FUNCIONAMENTO:
 * - Intercepta a criação de beans no contexto de teste
 * - Detecta beans anotados com @Repository
 * - Substitui por mocks do Mockito automaticamente
 * 
 * 🔧 VANTAGENS:
 * - Zero configuração manual
 * - Funciona para novos repositórios automaticamente
 * - Não quebra quando adiciona novos repositórios
 * - Manutenção zero
 */
@TestConfiguration
public class AutoMockRepositoryConfiguration implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("🔍 === CONFIGURAÇÃO AUTOMÁTICA DE MOCK DE REPOSITÓRIOS ===");
        
        // Obtém todos os nomes de beans
        String[] beanNames = beanFactory.getBeanDefinitionNames();
        int mockCount = 0;
        
        for (String beanName : beanNames) {
            try {
                // Obtém a classe do bean
                Class<?> beanClass = beanFactory.getType(beanName);
                
                if (beanClass != null && beanClass.isAnnotationPresent(Repository.class)) {
                    // Registra como mock
                    System.out.println("🎭 Mockando repositório: " + beanClass.getSimpleName());
                    
                    // Cria um mock do Mockito
                    Object mockBean = Mockito.mock(beanClass);
                    
                    // Registra o mock como singleton no contexto
                    beanFactory.registerSingleton(beanName, mockBean);
                    
                    mockCount++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        System.out.println("✅ Total de repositórios mockados automaticamente: " + mockCount);
        System.out.println("🔧 Para fazer mock explícito, injete com @Autowired e use when()");
        System.out.println("===============================================");
    }
}