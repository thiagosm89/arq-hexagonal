# Guia Rápido - Arquitetura Hexagonal

## 🚀 Start Rápido

```bash
# Compilar
./gradlew build

# Executar
./gradlew :application:bootRun

# Acessar
http://localhost:8080/api/usuarios
```

## 📐 Estrutura em 3 Módulos

```
┌──────────────────┐
│   APPLICATION    │  ← Configuração + Driving Adapters (REST)
│  - Controllers   │
│  - Services      │
│  - Config        │
└────────┬─────────┘
         │ conhece
         ↓
┌──────────────────┐
│   INFRASTRUCTURE │  ← Driven Adapters (JPA, etc.)
│  - JPA Adapters  │
│  - Entities      │
└────────┬─────────┘
         │ conhece
         ↓
┌──────────────────┐
│     DOMAIN       │  ← Lógica de Negócio PURA
│  - Entities      │     (SEM Spring!)
│  - Value Objects │
│  - Use Cases     │
│  - Ports         │
└──────────────────┘
```

## 🎯 Conceitos Principais

### 1. Portas (Interfaces)

```java
// Porta de ENTRADA (InboundPort)
public interface UsuarioInboundPort {
    Usuario criarUsuario(String nome, Email email, CPF cpf);
}

// Porta de SAÍDA (OutboundPort)
public interface UsuarioOutboundPort {
    Usuario salvar(Usuario usuario);
}
```

### 2. Adaptadores

```java
// Driving Adapter (Application) - chama Domain
@RestController
public class UsuarioController {
    private final UsuarioInboundPort inboundPort; // Chama
}

// Driven Adapter (Infrastructure) - chamado por Domain
@Component
public class UsuarioRepositoryAdapter implements UsuarioOutboundPort {
    // Implementa OutboundPort
}
```

### 3. Value Objects

```java
// Imutável, auto-validável, rico em comportamento
Email email = Email.of("joao@test.com");
email.getDomain();           // "test.com"
email.isFromDomain("test.com"); // true

CPF cpf = CPF.of("123.456.789-09");
cpf.getFormatted();  // "123.456.789-09"
cpf.getMasked();     // "***.***. 789-09"
```

### 4. CQRS

```java
// Commands (Write) → SEMPRE passam por Domain
@PostMapping
public Response criar(...) {
    commandService.criar(...); // → Domain
}

// Queries (Read) → PODEM fazer bypass
@GetMapping
public List<Response> listar() {
    queryService.listar(); // → Infrastructure (direto)
}
```

## 📂 Onde Colocar Cada Coisa?

| O Que | Onde | Por Quê |
|-------|------|---------|
| Entidades | `domain/model/` | Lógica de negócio |
| Value Objects | `domain/valueobject/` | Conceitos com validação |
| Use Cases | `domain/usecase/` | Orquestração (É negócio!) |
| Ports IN | `domain/ports/in/` | Interface de entrada |
| Ports OUT | `domain/ports/out/` | Interface de saída |
| Controllers | `application/rest/` | Driving Adapters |
| Command/Query Services | `application/service/` | CQRS |
| Config | `application/config/` | Beans manuais |
| JPA Entities | `infrastructure/persistence/entity/` | Detalhes técnicos |
| JPA Adapters | `infrastructure/persistence/adapter/` | Driven Adapters |

## ⚠️ Regras Críticas

### ❌ NUNCA Faça Isso

```java
// ❌ Spring no Domain
@Service
public class UsuarioUseCaseImpl { }

// ❌ Nomes acoplados
public interface UsuarioRepository { } // "Repository" indica BD

// ❌ Controllers na Infrastructure
infrastructure/rest/UsuarioController.java

// ❌ Primitivos nas Ports
Usuario criarUsuario(String email); // Use Value Object!
```

### ✅ SEMPRE Faça Isso

```java
// ✅ Domain puro
public class UsuarioUseCaseImpl { } // Sem @Service

// ✅ Nomes genéricos
public interface UsuarioOutboundPort { } // Genérico

// ✅ Controllers no Application
application/rest/UsuarioController.java

// ✅ Value Objects nas Ports
Usuario criarUsuario(Email email); // Type safe!
```

## 🎨 Padrões Implementados

```
✅ Arquitetura Hexagonal (Ports & Adapters)
✅ Clean Architecture (Domain puro)
✅ DDD (Value Objects, Entidades)
✅ CQRS (Command/Query Separation)
✅ Dependency Inversion (Domain não depende de nada)
```

## 🔍 Como Verificar Pureza

```bash
# Domain não deve ter Spring
grep -r "org.springframework" domain/src/main/java/
# (deve retornar vazio)

# Executar testes arquiteturais
./gradlew :domain:test --tests ArchitectureTest
```

## 📚 Documentação Completa

| Arquivo | Conteúdo |
|---------|----------|
| `README.md` | Visão geral do projeto |
| `ARCHITECTURE.md` | Diagramas detalhados |
| `CQRS.md` | Padrão CQRS explicado |
| `VALUE-OBJECTS.md` | O que são e como usar |
| `DOMAIN-PURITY.md` | Como manter Domain puro |
| `PORTS-NOMENCLATURE.md` | Nomenclatura correta |
| `PORT-DESIGN-DECISIONS.md` | Por que Value Objects nas Ports |
| `BEST-PRACTICES-SUMMARY.md` | Todas as decisões |
| `INTEGRACAO-VALUE-OBJECTS.md` | Integração Usuario/Email/CPF |

## 🎯 Exemplo Rápido

### Criar um novo recurso

```java
// 1. Domain: Criar Value Object
public final class Telefone {
    private final String value;
    public static Telefone of(String telefone) {
        // Validar
        return new Telefone(telefone);
    }
}

// 2. Domain: Criar Port
public interface ProdutoInboundPort {
    Produto criar(String nome, Money preco);
}

// 3. Domain: Implementar Use Case
public class ProdutoUseCaseImpl implements ProdutoInboundPort {
    // Lógica PURA
}

// 4. Application: Configurar Bean
@Bean
public ProdutoInboundPort produtoInboundPort(...) {
    return new ProdutoUseCaseImpl(...);
}

// 5. Application: Criar Controller
@RestController
public class ProdutoController {
    private final ProdutoInboundPort inboundPort;
}

// 6. Infrastructure: Criar Adapter
@Component
public class ProdutoJpaAdapter implements ProdutoOutboundPort {
    // Implementação JPA
}
```

## 💡 Dicas

### Para Application
- Converta Strings → Value Objects
- Use CommandService (write) e QueryService (read)
- Controllers apenas delegam

### Para Domain
- NUNCA importe Spring
- Use Value Objects para validação
- Use Cases focam em lógica

### Para Infrastructure
- Converta Value Objects → tipos técnicos
- Só implementa Driven Adapters (OUT)
- Sem Controllers aqui!

## 🎓 Conceitos em Uma Linha

| Conceito | Explicação |
|----------|------------|
| **InboundPort** | Interface que Application chama no Domain |
| **OutboundPort** | Interface que Domain chama (implementada por Infrastructure) |
| **Value Object** | Objeto imutável validado (Email, CPF) |
| **Entidade** | Objeto com identidade (Usuario) |
| **Driving Adapter** | Inicia ação (Controller) → Application |
| **Driven Adapter** | É chamado (Repository) → Infrastructure |
| **Command** | Operação write → passa por Domain |
| **Query** | Operação read → pode fazer bypass |
| **Bypass** | Pular Domain (só queries simples) |

## 🏆 Você Implementou

```
✅ Arquitetura Hexagonal PURA
✅ Domain 100% Puro (protegido)
✅ Value Objects (Email, CPF)
✅ CQRS com Bypass
✅ Portas com nomes genéricos
✅ Use Cases no Domain
✅ Controllers no Application
✅ Value Objects nas Ports (type safe)
✅ Testes arquiteturais
✅ Documentação completa
```

**Parabéns! Esta é uma arquitetura profissional e moderna! 🎉**

---

📖 **Leia:** BEST-PRACTICES-SUMMARY.md para entender TODAS as decisões  
🎯 **Próximo:** Adicione mais recursos seguindo o mesmo padrão

