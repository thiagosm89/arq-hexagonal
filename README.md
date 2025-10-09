# Arquitetura Hexagonal com Spring Boot

Projeto Spring Boot implementando Arquitetura Hexagonal (Ports and Adapters) com 3 módulos Gradle.

## 📐 Arquitetura

O projeto está dividido em 3 módulos seguindo os princípios da Arquitetura Hexagonal:

### 1. **Domain** (Núcleo)
- **Responsabilidade**: Contém toda a lógica de negócio pura
- **Dependências**: **NENHUMA** (não conhece outros módulos, sem frameworks)
- **Pureza**: ❌ **SEM Spring**, **SEM JPA**, **SEM frameworks** - apenas Java puro
- **Proteção**: Build gradle configurado para bloquear dependências proibidas
- **Nomenclatura**: Usa nomes genéricos e agnósticos (InboundPort/OutboundPort)
- **Contém**:
  - Entidades de domínio (`model`)
  - **Value Objects** (`valueobject`) - Email, CPF (imutáveis, auto-validáveis)
  - **Implementação da lógica de negócio** (`usecase`) - PURA, sem Spring
  - **InboundPort** (`ports.in`) - Portas de entrada (genéricas)
  - **OutboundPort** (`ports.out`) - Portas de saída (genéricas)
  - Exceções de domínio (`exception`)
  - **ArchitectureTest** - Testes que garantem pureza

### 2. **Infrastructure** (Adaptadores de Saída)
- **Responsabilidade**: Implementa os adaptadores de saída (driven)
- **Dependências**: Conhece **Domain**, mas NÃO conhece **Application**
- **Contém**:
  - Adaptadores de persistência (JPA)
  - Implementação de repositórios
  - Entidades JPA
  - Integrações com serviços externos

### 3. **Application** (Configuração e Adaptadores de Entrada)
- **Responsabilidade**: Configuração da aplicação, wiring e adaptadores de entrada (driving)
- **Dependências**: Conhece **Domain** e **Infrastructure**
- **Padrões**: CQRS (separação) + Bypass seletivo (otimização)
- **Fronteira transacional**: `@Transactional` nos Services, não nos Controllers
- **Contém**:
  - **@Configuration** para criar beans dos use cases manualmente
  - Adaptadores de entrada (REST Controllers)
  - **CommandService** - Operações de escrita (passam por Domain)
  - **QueryService** - Operações de leitura (podem fazer bypass)
  - DTOs de comunicação
  - Configuração Spring Boot
  - Handler de exceções
  - Classe principal

## 🚀 Como Executar

### Pré-requisitos
- Java 17 ou superior
- Gradle 7.x ou superior (ou usar o wrapper incluído)

### Compilar o projeto
```bash
./gradlew build
```

### Executar a aplicação
```bash
./gradlew :application:bootRun
```

Ou compile e execute o JAR:
```bash
./gradlew :application:bootJar
java -jar application/build/libs/application-0.0.1-SNAPSHOT.jar
```

A aplicação estará disponível em: `http://localhost:8080`

## 📡 Endpoints da API

### Criar Usuário
```bash
POST http://localhost:8080/api/usuarios
Content-Type: application/json

{
  "nome": "João Silva",
  "email": "joao@example.com"
}
```

### Buscar Usuário por ID
```bash
GET http://localhost:8080/api/usuarios/{id}
```

### Listar Todos os Usuários
```bash
GET http://localhost:8080/api/usuarios
```

### Buscar por Email
```bash
GET http://localhost:8080/api/usuarios/email/joao@example.com
```

### Contar Usuários
```bash
GET http://localhost:8080/api/usuarios/count
```

### Atualizar Usuário
```bash
PUT http://localhost:8080/api/usuarios/{id}
Content-Type: application/json

{
  "nome": "João Silva Atualizado",
  "email": "joao.atualizado@example.com"
}
```

### Remover Usuário
```bash
DELETE http://localhost:8080/api/usuarios/{id}
```

## 🗄️ Banco de Dados

O projeto usa H2 Database (em memória) para desenvolvimento.

Console H2: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: (vazio)

## 📊 Estrutura de Pastas

```
arq-hexagonal/
├── domain/                          # Módulo de Domínio (PURO)
│   └── src/main/java/com/example/domain/
│       ├── model/                   # Entidades de domínio
│       │   └── Usuario.java
│       ├── valueobject/             # Value Objects (imutáveis)
│       │   ├── Email.java
│       │   └── CPF.java
│       ├── usecase/                 # Implementação da lógica de negócio (SEM Spring)
│       │   └── UsuarioUseCaseImpl.java
│       ├── ports/
│       │   ├── in/                  # InboundPort (genérico, não "UseCase")
│       │   │   └── UsuarioInboundPort.java
│       │   └── out/                 # OutboundPort (genérico, não "Repository")
│       │       └── UsuarioOutboundPort.java
│       └── exception/               # Exceções de domínio
│
├── infrastructure/                  # Módulo de Infraestrutura (Driven Adapters)
│   └── src/main/java/com/example/infrastructure/
│       └── persistence/
│           ├── entity/              # Entidades JPA
│           ├── repository/          # Repositórios JPA
│           └── adapter/             # Adaptadores de persistência
│
└── application/                     # Módulo de Aplicação (Config + Driving Adapters)
    └── src/main/java/com/example/application/
        ├── rest/                    # Adaptadores REST (entrada)
        │   ├── dto/                 # DTOs REST
        │   │   ├── UsuarioRequest.java
        │   │   └── UsuarioResponse.java
        │   └── UsuarioController.java
        ├── service/                 # CQRS - Separação Command/Query
        │   ├── command/             # Commands (write) - passam por Domain
        │   │   └── UsuarioCommandService.java
        │   └── query/               # Queries (read) - podem fazer bypass
        │       ├── UsuarioQueryService.java
        │       └── dto/
        │           └── UsuarioListResponse.java
        ├── config/                  # Configurações Spring
        │   ├── BeanConfiguration.java
        │   └── UseCaseConfiguration.java  # Cria beans manualmente
        ├── exception/               # Handler de exceções
        └── Application.java         # Classe principal
```

## 🔒 Regras de Dependência

```
┌─────────────────┐
│   Application   │  ← Configuração e Bootstrap
│   (@Config)     │
└────────┬────────┘
         │ conhece
         ↓
    ┌────────────────────┐
    │   Infrastructure   │
    │    (Adaptadores)   │
    └────────┬───────────┘
             │ conhece
             ↓
        ┌─────────┐
        │  Domain │  ← Núcleo PURO (casos de uso + entidades)
        │ (Lógica)│     SEM frameworks!
        └─────────┘
```

### ⚠️ Decisões Arquiteturais Importantes

#### 1. Lógica de Negócio no Domain
A **lógica de negócio está no Domain**, não no Application. Isso porque:
- A orquestração da lógica de negócio **É PARTE DO NEGÓCIO**
- Para manter o Domain puro (sem Spring), o Application cria os beans manualmente via `@Configuration`
- Resultado: Domain 100% puro + Spring Boot funcional

Ver `UseCaseConfiguration.java` no módulo Application para detalhes.

#### 2. Nomenclatura de Portas
As portas usam nomes **genéricos e agnósticos**:
- ✅ **InboundPort** (não "UseCase") - pode ser qualquer entrada
- ✅ **OutboundPort** (não "Repository") - pode ser BD, REST, mensageria, etc.
- Isso mantém o Domain **completamente independente** de tecnologia

Ver `PORTS-NOMENCLATURE.md` para detalhes completos.

#### 3. Domain 100% Puro
O Domain **não pode ter dependências de frameworks**:
- ❌ **SEM** importações de `org.springframework.*`
- ❌ **SEM** anotações `@Service`, `@Component`, `@Autowired`
- ❌ **SEM** JPA (`@Entity`, `@Table`, etc.)
- ✅ **APENAS** Java puro e bibliotecas essenciais

**Proteções implementadas:**
- `domain/build.gradle` - Bloqueia dependências proibidas no build
- `ArchitectureTest.java` - Testes que verificam pureza automaticamente

Ver `DOMAIN-PURITY.md` para guia completo.

#### 4. CQRS (Separação Command/Query)
Implementamos **CQRS** (Command Query Responsibility Segregation):
- **Commands** (POST, PUT, DELETE) → `CommandService` → Domain
- **Queries** (GET) → `QueryService` → Domain ou Infrastructure

**Vantagens:**
- ✅ Responsabilidades separadas
- ✅ Código mais organizado
- ✅ Fácil otimizar cada lado independentemente

#### 5. Bypass em Queries (Otimização Adicional)
Queries **simples** podem fazer **bypass** do Domain:
- **Queries com lógica** → Passam por Domain
- **Queries simples** (listagens, buscas) → Bypass (direto à Infrastructure)

**Vantagens:**
- ✅ Performance otimizada (menos camadas)
- ✅ Commands sempre garantidos pelo Domain

Ver `CQRS.md` para documentação completa.

## 🧪 Testes

Para executar os testes:
```bash
./gradlew test
```

## 🛠️ Tecnologias Utilizadas

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- H2 Database
- Lombok
- Gradle (Multi-module)

## 🎨 Padrões e Arquiteturas Implementados

- ✅ **Arquitetura Hexagonal** (Ports & Adapters) - Base principal
- ✅ **Domain-Driven Design (DDD)** - Value Objects, Entidades
- ✅ **CQRS** (Command Query Responsibility Segregation) - Separação Command/Query
- ✅ **Bypass Seletivo** - Queries simples otimizadas (complementa CQRS)
- ✅ **Dependency Inversion Principle** (Domain não depende de nada)
- ✅ **Configuração Manual de Beans** (sem poluir Domain)
- ✅ **Domain Purity** (Build protegido contra frameworks)
- ✅ **Testes Arquiteturais** (Verificação automática de pureza)

**Nota:** Embora use princípios da Clean Architecture (Domain puro, Use Cases), a estrutura principal é **Hexagonal**.

## 📝 Licença

Este é um projeto de exemplo para fins educacionais.

