# Nomenclatura de Portas na Arquitetura Hexagonal

## 🎯 Por que InboundPort e OutboundPort?

Na Arquitetura Hexagonal **pura**, as portas devem ter nomes **genéricos e agnósticos** de tecnologia.

### ❌ Nomenclatura INCORRETA (acoplada)

```java
// ❌ "UseCase" já indica uma implementação específica
public interface UsuarioUseCase { }

// ❌ "Repository" já indica tecnologia (JPA/Database)
public interface UsuarioRepository { }
```

**Problemas:**
- `UseCase` pressupõe que sempre será um caso de uso
- `Repository` pressupõe que sempre será um banco de dados
- Viola o princípio de **agnóstico de tecnologia** do Domain

### ✅ Nomenclatura CORRETA (genérica)

```java
// ✅ "InboundPort" - genérico, qualquer entrada
public interface UsuarioInboundPort { }

// ✅ "OutboundPort" - genérico, qualquer saída
public interface UsuarioOutboundPort { }
```

**Vantagens:**
- Nomes genéricos e neutros
- Não indicam implementação específica
- Domain completamente independente de tecnologia
- Flexibilidade para múltiplas implementações

## 📊 Comparação

| Aspecto | UseCase/Repository | InboundPort/OutboundPort |
|---------|-------------------|-------------------------|
| **Independência** | ❌ Acoplado | ✅ Independente |
| **Flexibilidade** | ❌ Limitado | ✅ Flexível |
| **Clareza** | ⚠️ Assume tecnologia | ✅ Genérico |
| **Hexagonal Puro** | ❌ Não | ✅ Sim |

## 🔄 Terminologia

### Portas de Entrada (Inbound Ports)

**Também chamadas de:**
- Inbound Ports
- Input Ports
- Primary Ports
- Driving Ports

**O que são:**
- Interfaces que definem **o que a aplicação oferece**
- Chamadas **de fora para dentro** (driving)
- Implementadas pelo **Domain**
- Usadas pelos **Driving Adapters** (Controllers, CLI, etc.)

**Exemplos de implementações:**
```java
// A porta é genérica
public interface UsuarioInboundPort {
    Usuario criarUsuario(String nome, String email);
}

// Pode ter diferentes implementações:
class UsuarioUseCaseImpl implements UsuarioInboundPort { } // Caso de uso
class UsuarioServiceImpl implements UsuarioInboundPort { }  // Serviço
class UsuarioHandlerImpl implements UsuarioInboundPort { }  // Handler
```

### Portas de Saída (Outbound Ports)

**Também chamadas de:**
- Outbound Ports
- Output Ports
- Secondary Ports
- Driven Ports

**O que são:**
- Interfaces que definem **o que o Domain precisa**
- Chamadas **de dentro para fora** (driven)
- Definidas pelo **Domain**
- Implementadas pela **Infrastructure**

**Exemplos de implementações:**
```java
// A porta é genérica
public interface UsuarioOutboundPort {
    Usuario salvar(Usuario usuario);
    Optional<Usuario> buscarPorId(Long id);
}

// Pode ter MÚLTIPLAS implementações diferentes:
class UsuarioJpaAdapter implements UsuarioOutboundPort { }        // Banco de dados
class UsuarioRestClientAdapter implements UsuarioOutboundPort { } // API externa
class UsuarioKafkaAdapter implements UsuarioOutboundPort { }      // Mensageria
class UsuarioCacheAdapter implements UsuarioOutboundPort { }      // Cache
class UsuarioFileAdapter implements UsuarioOutboundPort { }       // Arquivo
```

## 🎨 Diagrama

```
┌────────────────────────────────────────────┐
│          Driving Adapters                  │
│     (Controllers, CLI, GraphQL)            │
└──────────────────┬─────────────────────────┘
                   │ chama
                   ↓
         ┌─────────────────────┐
         │   InboundPort       │  ← Porta de ENTRADA
         │   (Interface)       │     (Driving/Primary)
         └─────────────────────┘
                   │ implementado por
                   ↓
┌────────────────────────────────────────────┐
│              DOMAIN                        │
│                                            │
│  Lógica de Negócio (UsuarioUseCaseImpl)   │
│                                            │
└──────────────────┬─────────────────────────┘
                   │ usa
                   ↓
         ┌─────────────────────┐
         │   OutboundPort      │  ← Porta de SAÍDA
         │   (Interface)       │     (Driven/Secondary)
         └─────────────────────┘
                   │ implementado por
                   ↓
┌────────────────────────────────────────────┐
│        Driven Adapters                     │
│  (JPA, REST Client, Kafka, Cache, etc.)   │
└────────────────────────────────────────────┘
```

## 📝 Exemplos Práticos

### Inbound Port (Entrada)

```java
package com.example.domain.ports.in;

/**
 * Porta de Entrada - Agnóstica de tecnologia
 * Não é "UseCase" - pode ser implementada como:
 * - Use Case
 * - Service
 * - Handler
 * - Command
 * - Qualquer lógica de negócio
 */
public interface UsuarioInboundPort {
    Usuario criarUsuario(String nome, String email);
    Usuario buscarUsuarioPorId(Long id);
    void removerUsuario(Long id);
}
```

### Outbound Port (Saída)

```java
package com.example.domain.ports.out;

/**
 * Porta de Saída - Agnóstica de tecnologia
 * Não é "Repository" - pode ser implementada por:
 * - JPA Repository (banco de dados)
 * - REST Client (API externa)
 * - Kafka Producer (mensageria)
 * - Redis Client (cache)
 * - File System (arquivos)
 * - Qualquer recurso externo
 */
public interface UsuarioOutboundPort {
    Usuario salvar(Usuario usuario);
    Optional<Usuario> buscarPorId(Long id);
    void deletar(Long id);
}
```

## 🔧 Implementações

### Domain (implementa InboundPort)

```java
package com.example.domain.usecase;

public class UsuarioUseCaseImpl implements UsuarioInboundPort {
    
    private final UsuarioOutboundPort outboundPort;
    
    public UsuarioUseCaseImpl(UsuarioOutboundPort outboundPort) {
        this.outboundPort = outboundPort;
    }
    
    @Override
    public Usuario criarUsuario(String nome, String email) {
        // Lógica de negócio pura
        Usuario usuario = new Usuario(nome, email);
        return outboundPort.salvar(usuario);
    }
}
```

### Infrastructure (implementa OutboundPort)

```java
package com.example.infrastructure.persistence.adapter;

@Component
public class UsuarioJpaAdapter implements UsuarioOutboundPort {
    
    private final UsuarioJpaRepository jpaRepository;
    
    @Override
    public Usuario salvar(Usuario usuario) {
        // Implementação específica com JPA
        UsuarioEntity entity = toEntity(usuario);
        entity = jpaRepository.save(entity);
        return toDomain(entity);
    }
}
```

### Application (usa InboundPort)

```java
package com.example.application.service;

@Service
public class UsuarioCommandService {
    
    private final UsuarioInboundPort inboundPort; // Usa a porta!
    
    public UsuarioResponse criarUsuario(UsuarioRequest request) {
        Usuario usuario = inboundPort.criarUsuario(
            request.getNome(), 
            request.getEmail()
        );
        return toResponse(usuario);
    }
}
```

## 🎁 Benefícios

### 1. Independência de Tecnologia
```
Domain não sabe se OutboundPort é:
- Database
- REST API
- Kafka
- Cache
- Arquivo
```

### 2. Flexibilidade
```
Posso ter múltiplas implementações:
- UsuarioJpaAdapter (produção)
- UsuarioMockAdapter (testes)
- UsuarioRestAdapter (integração)
```

### 3. Testabilidade
```java
// Fácil mockar as portas
UsuarioInboundPort mockInbound = mock(UsuarioInboundPort.class);
UsuarioOutboundPort mockOutbound = mock(UsuarioOutboundPort.class);
```

### 4. Domain Puro
```
Domain só conhece:
- Suas próprias entidades
- Suas próprias portas (interfaces)
- Nada de frameworks ou tecnologias
```

## 📚 Referências

- **Alistair Cockburn** - Hexagonal Architecture (2005)
  - Termo original: "Ports and Adapters"
  - Portas são interfaces genéricas
  - Adaptadores são implementações específicas

- **Robert C. Martin** - Clean Architecture
  - Dependency Rule
  - Abstrações não devem depender de detalhes

## ✅ Checklist de Nomenclatura

- [ ] InboundPort - nome genérico ✅
- [ ] OutboundPort - nome genérico ✅
- [ ] Sem referência a tecnologia (Repository, REST, etc.) ✅
- [ ] Interfaces no Domain ✅
- [ ] Implementações fora do Domain ✅
- [ ] Domain não conhece Spring, JPA, etc. ✅

## 🎯 Resumo

| Nome | Localização | Implementado Por | Usado Por |
|------|-------------|------------------|-----------|
| **InboundPort** | domain/ports/in | Domain | Application |
| **OutboundPort** | domain/ports/out | Infrastructure | Domain |

**Regra de ouro:** Nomes de portas devem ser **genéricos** e **agnósticos** de tecnologia!

