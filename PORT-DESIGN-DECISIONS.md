# Decisões de Design: InboundPort com Value Objects

## 🤔 Pergunta Fundamental

**InboundPort deve receber Strings ou Value Objects?**

## 📊 Comparação das Abordagens

### ❌ Abordagem 1: Receber Strings Primitivas

```java
// Port recebe primitivos
public interface UsuarioInboundPort {
    Usuario criarUsuario(String nome, String email, String cpf);
}

// Application passa strings
usuarioInboundPort.criarUsuario("João", "joao@test.com", "123.456.789-09");

// UseCase converte para Value Objects
public Usuario criarUsuario(String nome, String email, String cpf) {
    Email emailVO = Email.of(email);  // Validação aqui
    CPF cpfVO = CPF.of(cpf);
    return new Usuario(nome, emailVO, cpfVO);
}
```

**Problemas:**
- ❌ Sem type safety (pode passar valores errados)
- ❌ Validação tardia (só no UseCase)
- ❌ Port menos expressivo
- ❌ Conversão duplicada se múltiplos use cases usam Email

### ✅ Abordagem 2: Receber Value Objects (RECOMENDADA)

```java
// Port recebe Value Objects
public interface UsuarioInboundPort {
    Usuario criarUsuario(String nome, Email email, CPF cpf);
}

// Application converte e passa Value Objects
Email email = Email.of("joao@test.com");  // Validação aqui
CPF cpf = CPF.of("123.456.789-09");
usuarioInboundPort.criarUsuario("João", email, cpf);

// UseCase recebe objetos já validados
public Usuario criarUsuario(String nome, Email email, CPF cpf) {
    // Email e CPF já validados!
    return new Usuario(nome, email, cpf);
}
```

**Vantagens:**
- ✅ Type safety (compilador garante tipos corretos)
- ✅ Validação mais cedo (fail fast)
- ✅ Port mais expressivo
- ✅ UseCase focado em lógica de negócio
- ✅ Application reutiliza Value Objects

## 🎯 Resposta: **SIM, é uma BOA PRÁTICA!**

### Por quê?

#### 1. **Application DEVE conhecer Domain**
```
Arquitetura Hexagonal:
Application → conhece → Domain
Application → conhece → Infrastructure

Então pode usar Value Objects do Domain!
```

#### 2. **Separação de Responsabilidades**
```
Application (Adapter):
  - Recebe dados externos (HTTP, CLI)
  - Converte para tipos do Domain
  - Valida formato (Value Objects)

Domain (UseCase):
  - Recebe objetos já validados
  - Foca em LÓGICA DE NEGÓCIO
  - Aplica regras (email duplicado, etc.)
```

#### 3. **Type Safety**
```java
// ❌ Primitivos: pode confundir
criarUsuario(email, nome, cpf); // Compilador não detecta!

// ✅ Value Objects: impossível confundir
criarUsuario(Email, Nome, CPF); // Erro de compilação!
```

#### 4. **Fail Fast**
```
String → Value Object no Application
    ↓ (se inválido, falha AQUI)
    
UseCase nem é chamado se Value Object inválido!
```

## 📈 Fluxo Comparativo

### Strings (Abordagem 1)

```
HTTP Request "joao@test.com"
    ↓
UsuarioRequest { email: "joao@test.com" }
    ↓
CommandService passa String
    ↓
InboundPort recebe String
    ↓
UseCase valida String → Email    ← Validação AQUI
    ↓
Usuario com Email
```

### Value Objects (Abordagem 2) ✅

```
HTTP Request "joao@test.com"
    ↓
UsuarioRequest { email: "joao@test.com" }
    ↓
CommandService valida String → Email  ← Validação AQUI (cedo)
    ↓
InboundPort recebe Email
    ↓
UseCase usa Email direto         ← Já validado!
    ↓
Usuario com Email
```

## 🎨 Exemplo Completo

### Domain (Port)

```java
package com.example.domain.ports.in;

import com.example.domain.valueobject.Email;
import com.example.domain.valueobject.CPF;

/**
 * ✅ Port expressivo e type-safe
 */
public interface UsuarioInboundPort {
    // Deixa claro que espera Value Objects
    Usuario criarUsuario(String nome, Email email, CPF cpf);
}
```

### Domain (UseCase)

```java
package com.example.domain.usecase;

public class UsuarioUseCaseImpl implements UsuarioInboundPort {
    
    @Override
    public Usuario criarUsuario(String nome, Email email, CPF cpf) {
        // Email e CPF já validados!
        // Foco na LÓGICA DE NEGÓCIO
        
        Usuario usuario = new Usuario(nome, email, cpf);
        
        // Verifica regra: email duplicado
        if (emailJaExiste(email)) {
            throw new UsuarioInvalidoException("Email já cadastrado");
        }
        
        return repository.salvar(usuario);
    }
}
```

### Application (Service)

```java
package com.example.application.service.command;

@Service
public class UsuarioCommandService {
    
    private final UsuarioInboundPort inboundPort;
    
    public UsuarioResponse criarUsuario(UsuarioRequest request) {
        // Application faz a conversão
        Email email = Email.of(request.getEmail());  // Valida
        CPF cpf = request.getCpf() != null 
            ? CPF.of(request.getCpf())  // Valida
            : null;
        
        // Chama Port com Value Objects
        Usuario usuario = inboundPort.criarUsuario(
            request.getNome(),
            email,  // Value Object
            cpf     // Value Object
        );
        
        return toResponse(usuario);
    }
}
```

## ⚖️ Trade-offs

### Vantagens

| Aspecto | Benefício |
|---------|-----------|
| **Type Safety** | Compilador garante tipos corretos |
| **Validação** | Fail fast no Application |
| **Expressividade** | Port autodocumentado |
| **Reutilização** | Application cria Value Objects uma vez |
| **Foco** | UseCase foca em lógica, não em conversão |

### Considerações

| Aspecto | Como Lidar |
|---------|-----------|
| **Múltiplos Adapters** | Cada adapter converte para Value Objects |
| **Adapter Simples** | CLI, GraphQL também conhecem Domain |
| **Dependência** | Application precisa importar valueobjects |

## 🎯 Quando Usar Cada Abordagem?

### Use Value Objects na Port quando:

- ✅ Application conhece Domain (arquitetura hexagonal)
- ✅ Tem validações complexas (CPF, Email)
- ✅ Quer type safety máximo
- ✅ Múltiplos use cases usam os mesmos tipos
- ✅ **RECOMENDADO** para arquiteturas maduras

### Use Strings na Port quando:

- ⚠️ Adaptadores externos não podem depender do Domain
- ⚠️ API pública exposta para terceiros
- ⚠️ Arquitetura muito simples sem Value Objects
- ⚠️ Poucos casos (não compensa Value Objects)

## 🏆 Decisão Arquitetural

### Para este projeto: **Value Objects na Port! ✅**

**Justificativa:**
1. Application conhece Domain (arquitetura hexagonal)
2. Temos Value Objects ricos (Email, CPF)
3. Queremos type safety máximo
4. Domain puro e focado em lógica

**Resultado:**
```java
// ✅ Port expressivo e seguro
Usuario criarUsuario(String nome, Email email, CPF cpf);

// Application converte
Email email = Email.of(request.getEmail());

// UseCase recebe pronto
public Usuario criarUsuario(String nome, Email email, CPF cpf) {
    // Email já validado, foca em lógica!
}
```

## 📚 Referências

- **Clean Architecture** (Uncle Bob): "Boundaries têm tipos ricos"
- **DDD** (Eric Evans): "Value Objects atravessam boundaries"
- **Hexagonal Architecture**: "Ports podem usar tipos do Domain"

## ✅ Conclusão

**SIM, é uma EXCELENTE prática!**

### Benefícios Conquistados:

```
✅ Type Safety
✅ Validação Cedo
✅ Port Expressivo
✅ UseCase Focado em Lógica
✅ Reutilização de Value Objects
✅ Código mais Seguro
✅ Menos Conversões Duplicadas
```

### Princípio Aplicado:

> **"Application conhece Domain, então PODE e DEVE usar seus tipos ricos (Value Objects) nas chamadas às Ports."**

---

**Decisão Arquitetural:** Usar Value Objects como parâmetros nas InboundPorts  
**Justificativa:** Type safety, validação cedo, expressividade  
**Status:** ✅ Implementado e Recomendado

