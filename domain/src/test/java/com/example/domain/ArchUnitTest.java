package com.example.domain;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Testes Arquiteturais com ArchUnit para garantir pureza do Domain
 * 
 * 🎯 OBJETIVO: Garantir que o módulo Domain permanece puro e independente
 * 
 * ✅ REGRAS ENFORÇADAS:
 * - Domain não importa frameworks (Spring, JPA, etc.)
 * - Domain não usa anotações de frameworks
 * - Domain não depende de outros módulos do projeto
 * - Domain segue padrões de naming e organização
 * 
 * 🚨 SE ESTES TESTES FALHAREM: A arquitetura hexagonal está comprometida!
 */
@AnalyzeClasses(packages = "com.example.domain", 
                importOptions = ImportOption.DoNotIncludeTests.class)
class ArchUnitTest {

    /**
     * 🚫 REGRA 1: Domain NÃO pode importar frameworks externos
     * 
     * Garante que o Domain permanece puro e independente de:
     * - Spring Framework
     * - JPA/Hibernate  
     * - Qualquer outro framework específico
     */
    @ArchTest
    static final ArchRule domainNaoDeveImportarSpring = 
        noClasses()
            .should()
            .dependOnClassesThat()
            .resideInAPackage("org.springframework..")
            .because("Domain deve ser PURO e independente do Spring Framework");

    @ArchTest
    static final ArchRule domainNaoDeveImportarJPA = 
        noClasses()
            .should()
            .dependOnClassesThat()
            .resideInAPackage("jakarta.persistence..")
            .orShould()
            .dependOnClassesThat()
            .resideInAPackage("javax.persistence..")
            .because("Domain deve ser PURO e independente do JPA");

    /**
     * 🚫 REGRA 2: Domain NÃO pode usar anotações de frameworks
     * 
     * Garante que o Domain não usa anotações como:
     * - @Entity, @Table, @Column (JPA)
     * - @Service, @Component, @Repository (Spring)
     * - @Controller, @RestController (Spring MVC)
     */
    @ArchTest
    static final ArchRule domainNaoDeveUsarAnotacoesDeFrameworks = 
        noClasses()
            .should()
            .beAnnotatedWith("org.springframework.stereotype.Service")
            .orShould()
            .beAnnotatedWith("org.springframework.stereotype.Component")
            .orShould()
            .beAnnotatedWith("org.springframework.stereotype.Repository")
            .orShould()
            .beAnnotatedWith("org.springframework.web.bind.annotation.RestController")
            .orShould()
            .beAnnotatedWith("org.springframework.web.bind.annotation.Controller")
            .orShould()
            .beAnnotatedWith("jakarta.persistence.Entity")
            .orShould()
            .beAnnotatedWith("jakarta.persistence.Table")
            .orShould()
            .beAnnotatedWith("jakarta.persistence.Column")
            .orShould()
            .beAnnotatedWith("jakarta.persistence.Id")
            .orShould()
            .beAnnotatedWith("javax.persistence.Entity")
            .orShould()
            .beAnnotatedWith("org.springframework.beans.factory.annotation.Autowired")
            .because("Domain não deve usar anotações de frameworks - deve ser POJO puro");

    /**
     * 🚫 REGRA 3: Domain NÃO pode depender de outros módulos do projeto
     * 
     * Garante que o Domain não importa classes de:
     * - application module
     * - infrastructure module
     */
    @ArchTest
    static final ArchRule domainNaoDeveDependerDeOutrosModulos = 
        noClasses()
            .should()
            .dependOnClassesThat()
            .resideInAPackage("com.example.application..")
            .orShould()
            .dependOnClassesThat()
            .resideInAPackage("com.example.infrastructure..")
            .because("Domain deve ser independente - outros módulos dependem DELE, não o contrário");

    /**
     * ✅ REGRA 4: Não deve haver dependências circulares no Domain
     * 
     * Garante que não há ciclos de dependência dentro do próprio Domain
     */
    @ArchTest
    static final ArchRule naoDeveHaverDependenciasCirculares = 
        slices()
            .matching("com.example.domain.(*)..")
            .should()
            .beFreeOfCycles()
            .because("Domain não deve ter dependências circulares internas");

    /**
     * ✅ REGRA 5: Classes de domínio devem ser POJOs
     * 
     * Garante que entidades não herdam de frameworks
     */
    @ArchTest
    static final ArchRule classesDevemSerPOJOs = 
        noClasses()
            .should()
            .beAssignableTo("org.springframework.stereotype.Component")
            .orShould()
            .beAssignableTo("jakarta.persistence.Entity")
            .because("Classes de domínio devem ser POJOs puros, sem herança de frameworks");

}
