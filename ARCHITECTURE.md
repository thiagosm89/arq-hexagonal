# Documentação da Arquitetura Hexagonal

## 📐 Visão Geral

Este projeto implementa a **Arquitetura Hexagonal** (também conhecida como **Ports and Adapters**), proposta por Alistair Cockburn. O objetivo principal é criar um sistema desacoplado, testável e independente de frameworks.

## 🎯 Princípios Fundamentais

### 1. Separação de Responsabilidades
- **Domain**: Lógica de negócio pura (entidades + casos de uso)
- **Infrastructure**: Adaptadores de saída/driven (BD, APIs externas, etc.)
- **Application**: Configuração, bootstrap e adaptadores de entrada/driving (REST, CLI, etc.)

### 2. Inversão de Dependências
O núcleo (Domain) não depende de nada. Todos os outros módulos dependem dele através de interfaces (portas).

### 3. Independência de Frameworks
A lógica de negócio não conhece Spring, JPA ou qualquer framework. Eles são apenas detalhes de implementação.

### 4. Domain Puro com Configuração Manual
**Decisão Arquitetural Importante:**
- Os **Use Cases ficam no Domain** (não no Application) porque a orquestração de regras de negócio É PARTE DO NEGÓCIO
- Para manter o Domain puro, os use cases **não têm anotações Spring** (sem @Service)
- No módulo **Application**, criamos uma classe `@Configuration` que instancia os use cases manualmente via `@Bean`
- Isso mantém a arquitetura **100% pura** enquanto permite o uso de Spring Boot

## 📊 Diagrama da Arquitetura

```
┌─────────────────────────────────────────────────────────┐
│                      APPLICATION                         │
│  ┌──────────────────────────────────────────────────┐  │
│  │         Configuração (Spring @Configuration)     │  │
│  │  - UseCaseConfiguration                          │  │
│  │  - Cria beans manualmente                        │  │
│  └──────────────────┬───────────────────────────────┘  │
│                     │ instancia                         │
│                     ↓                                   │
└─────────────────────────────────────────────────────────┘
                      │
                      │ configura
                      ↓
┌─────────────────────────────────────────────────────────┐
│                    APPLICATION                           │
│  ┌─────────────────────────┐                            │
│  │   Adapters de Entrada   │                            │
│  │   (Driving/Primary)     │                            │
│  │                         │                            │
│  │  - REST Controllers     │                            │
│  │  - UsuarioController    │                            │
│  └──────────┬──────────────┘                            │
│             │ chama                                      │
└─────────────┼──────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────────────────────┐
│                    INFRASTRUCTURE                        │
│  ┌────────────────────┐                                 │
│  │ Adapters de Saída  │                                 │
│  │ (Driven/Secondary) │                                 │
│  │                    │                                 │
│  │  - JPA Repositories│                                 │
│  │  - UsuarioRepo...  │                                 │
│  │    Adapter         │                                 │
│  └────────┬───────────┘                                 │
│           │ implementa                                  │
│           ↓                                             │
└─────────────────────────────────────────────────────────┘
               │                          │
               │                          │
               ↓                          ↓
┌─────────────────────────────────────────────────────────┐
│                        DOMAIN                            │
│  ┌──────────────────────────────────────────────────┐  │
│  │              Portas (Ports/Interfaces)           │  │
│  │  ┌────────────────┐      ┌──────────────────┐   │  │
│  │  │   Ports IN     │      │    Ports OUT     │   │  │
│  │  │ (Use Cases)    │      │  (Repositories)  │   │  │
│  │  │                │      │                  │   │  │
│  │  │UsuarioUseCase  │      │UsuarioRepository │   │  │
│  │  └────────┬───────┘      └──────────────────┘   │  │
│  │           │ implementado por ↓                   │  │
│  └───────────┼──────────────────────────────────────┘  │
│              │                                          │
│  ┌───────────┴──────────────────────────────────────┐  │
│  │      Casos de Uso (Use Cases) - PUROS           │  │
│  │  - UsuarioUseCaseImpl (sem @Service)            │  │
│  │  - Orquestração da lógica de negócio            │  │
│  └──────────────────────────────────────────────────┘  │
│                                                         │
│  ┌──────────────────────────────────────────────────┐  │
│  │            Modelo de Domínio                     │  │
│  │  - Entidades (Usuario)                           │  │
│  │  - Value Objects                                 │  │
│  │  - Lógica de Negócio                             │  │
│  │  - Exceções de Domínio                           │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

## 🔄 Fluxo de Dados

### Exemplo: Criar um Usuário

1. **Cliente HTTP** → Envia POST /api/usuarios
2. **UsuarioController** (Application) → Recebe requisição
3. **UsuarioController** → Chama `UsuarioUseCase.criarUsuario()`
4. **UsuarioUseCaseImpl** (Domain) → Executa lógica de negócio
5. **UsuarioUseCaseImpl** → Valida dados usando `Usuario.isValid()`
6. **UsuarioUseCaseImpl** → Chama `UsuarioRepository.salvar()`
7. **UsuarioRepositoryAdapter** (Infrastructure) → Converte para entidade JPA
8. **UsuarioJpaRepository** → Salva no banco de dados
9. **Resposta** ← Retorna pela cadeia inversa

```
HTTP Request
     │
     ↓
[UsuarioController] ────────────────────┐
     (Application)                      │  Driving Adapter
                                        │
                                        ↓
                         [UsuarioUseCase Interface]
                                (Domain - Port IN)
                                        │
                                        │ implementado por
                                        ↓
                            [UsuarioUseCaseImpl]
                                (Domain - PURO)
                                        │
                                        │ usa
                                        ↓
                         [UsuarioRepository Interface]
                                (Domain - Port OUT)
                                        │
                                        │ implementado por
                                        ↓
                         [UsuarioRepositoryAdapter]  ← Driven Adapter
                            (Infrastructure)
                                        │
                                        ↓
                                   [Database]
```

## 📦 Estrutura de Módulos

### Domain (PURO - Sem dependências de frameworks)
```
domain/
└── com.example.domain/
    ├── model/              # Entidades de domínio
    │   └── Usuario.java
    ├── usecase/           # Implementação dos casos de uso (PURA)
    │   └── UsuarioUseCaseImpl.java  # SEM @Service
    ├── ports/
    │   ├── in/            # Portas de entrada (interfaces de use cases)
    │   │   └── UsuarioUseCase.java
    │   └── out/           # Portas de saída (interfaces de repositórios)
    │       └── UsuarioRepository.java
    └── exception/         # Exceções de domínio
        ├── UsuarioNaoEncontradoException.java
        └── UsuarioInvalidoException.java
```

### Infrastructure (Adaptadores de Saída/Driven)
```
infrastructure/
└── com.example.infrastructure/
    └── persistence/
        ├── entity/        # Entidades JPA (detalhe técnico)
        │   └── UsuarioEntity.java
        ├── repository/    # Repositórios Spring Data
        │   └── UsuarioJpaRepository.java
        └── adapter/       # Adaptadores que implementam ports OUT
            └── UsuarioRepositoryAdapter.java
```

### Application (Configuração + Adaptadores de Entrada/Driving)
```
application/
└── com.example.application/
    ├── rest/              # Adaptadores de entrada (driving)
    │   ├── dto/           # DTOs para comunicação HTTP
    │   │   ├── UsuarioRequest.java
    │   │   └── UsuarioResponse.java
    │   └── UsuarioController.java  # Adaptador REST (driving)
    ├── config/            # Configurações Spring
    │   ├── BeanConfiguration.java
    │   └── UseCaseConfiguration.java  # Cria beans dos use cases
    ├── exception/         # Handlers de exceções
    │   └── GlobalExceptionHandler.java
    └── Application.java   # Classe principal Spring Boot
```

## 🎨 Conceitos Chave

### Portas (Ports)
Interfaces que definem contratos:
- **Ports IN**: O que a aplicação faz (use cases)
- **Ports OUT**: O que a aplicação precisa (repositórios, serviços externos)

### Adaptadores (Adapters)
Implementações concretas das portas:
- **Driving Adapters** (Primários/Entrada): Iniciam ações → Ficam no **Application** (Controllers REST, CLI, eventos)
- **Driven Adapters** (Secundários/Saída): São chamados pela aplicação → Ficam na **Infrastructure** (Repositórios, APIs externas)

### Domain Model
Entidades e lógica de negócio pura, sem dependências externas.

## 🔧 Configuração Manual dos Use Cases

Para manter o Domain puro (sem anotações Spring), usamos configuração manual:

**No Domain (Puro):**
```java
// domain/usecase/UsuarioUseCaseImpl.java
public class UsuarioUseCaseImpl implements UsuarioUseCase {
    
    private final UsuarioRepository repository;
    
    // Construtor simples - SEM @Autowired
    public UsuarioUseCaseImpl(UsuarioRepository repository) {
        this.repository = repository;
    }
    
    // Lógica de negócio pura...
}
```

**No Application (Configuração Spring):**
```java
// application/config/UseCaseConfiguration.java
@Configuration
public class UseCaseConfiguration {
    
    @Bean
    public UsuarioUseCase usuarioUseCase(UsuarioRepository repository) {
        // Instanciação manual do use case
        return new UsuarioUseCaseImpl(repository);
    }
}
```

**Resultado:**
- ✅ Domain completamente **puro** (sem Spring)
- ✅ Use cases onde **devem estar** (no Domain)
- ✅ Spring Boot **funciona perfeitamente**
- ✅ Fácil de **testar** (instanciação simples com `new`)

## ✅ Vantagens

1. **Testabilidade**: Fácil criar mocks das interfaces
2. **Flexibilidade**: Trocar implementações sem afetar o domínio
3. **Independência**: Domínio não depende de frameworks
4. **Manutenibilidade**: Responsabilidades bem definidas
5. **Escalabilidade**: Fácil adicionar novos adapters

## 🧪 Estratégia de Testes

- **Domain**: Testes unitários puros
- **Application**: Testes com mocks dos repositórios
- **Infrastructure**: Testes de integração
- **End-to-End**: Testes com toda a aplicação

## 📚 Referências

- [Hexagonal Architecture - Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [Clean Architecture - Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Domain-Driven Design - Eric Evans](https://www.domainlanguage.com/ddd/)

