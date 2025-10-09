# CQRS - Command Query Responsibility Segregation

## 🎯 O que é CQRS?

**CQRS** significa **Separação de Responsabilidade entre Comandos e Consultas**.

É um padrão arquitetural que separa operações de:
- **ESCRITA** (Commands)
- **LEITURA** (Queries)

## ⚠️ CQRS ≠ Bypass

**CQRS** = Separação de responsabilidades (dois services diferentes)
**Bypass** = Otimização onde queries pulam o Domain (opcional)

**Neste projeto:** CQRS (separação) + Bypass seletivo (otimização)

## 📊 Diagrama: CQRS

```
                    Controller
                        |
        ┌───────────────┴───────────────┐
        |                               |
        ↓                               ↓
  COMMANDS (Write)              QUERIES (Read)
        |                               |
        ↓                               ↓
CommandService                   QueryService
        |                               |
        ↓                               ↓
    DOMAIN                           DOMAIN
(Lógica de negócio)            (Lógica de consulta)
        |                               |
        ↓                               ↓
  Infrastructure                  Infrastructure
    (Repository)                    (Repository)
        |                               |
        └───────────────┬───────────────┘
                        ↓
                    Database
```

**CQRS = Separação de responsabilidades (Commands vs Queries)**

---

## ⚡ Diagrama: Bypass (Otimização Adicional)

```
                    Controller
                        |
        ┌───────────────┴───────────────┐
        |                               |
        ↓                               ↓
  COMMANDS (Write)              QUERIES (Read)
        |                               |
        ↓                               ↓
CommandService                   QueryService
        |                               |
        ↓                               |
    DOMAIN                              | BYPASS!
(Sempre passa)                          | (pula Domain)
        |                               ↓
        ↓                         Infrastructure
  Infrastructure                   (Direto ao banco)
    (Repository)                        |
        |                               |
        └───────────────┬───────────────┘
                        ↓
                    Database
```

**Bypass = Queries simples pulam Domain (otimização)**

## 🔀 Fluxos Implementados

### ✍️ Commands (Escrita) - Passa por Domain

```
POST /api/usuarios
    ↓
UsuarioController
    ↓
UsuarioCommandService ← Application Service
    ↓
UsuarioUseCase ← Interface (Port IN)
    ↓
UsuarioUseCaseImpl ← Domain (lógica de negócio)
    ↓
UsuarioRepository ← Interface (Port OUT)
    ↓
UsuarioRepositoryAdapter ← Infrastructure
    ↓
Database
```

**Características:**
- ✅ Passa pela camada de Domain
- ✅ Valida regras de negócio
- ✅ Executa lógica
- ✅ Pode lançar exceções de domínio

### 📖 Queries (Leitura) - Bypass Domain

```
GET /api/usuarios
    ↓
UsuarioController
    ↓
UsuarioQueryService ← Application Service
    ↓
UsuarioJpaRepository ← Infrastructure (DIRETO!)
    ↓
Database
```

**Características:**
- ✅ **BYPASS** da camada Domain
- ✅ Vai direto à Infrastructure
- ✅ Mais rápido (menos camadas)
- ✅ Apenas para queries SIMPLES

## 📝 Exemplos no Código

### Command Service (Write)

```java
@Service
public class UsuarioCommandService {
    private final UsuarioUseCase usuarioUseCase; // Usa Domain!
    
    public UsuarioResponse criarUsuario(UsuarioRequest request) {
        // PASSA por Domain - tem lógica!
        Usuario usuario = usuarioUseCase.criarUsuario(
            request.getNome(), 
            request.getEmail()
        );
        return toResponse(usuario);
    }
}
```

### Query Service (Read)

```java
@Service
public class UsuarioQueryService {
    private final UsuarioJpaRepository repository; // Usa Infrastructure direto!
    
    public List<UsuarioListResponse> listarTodosUsuarios() {
        // BYPASS Domain - query simples!
        return repository.findAll()
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
}
```

### Controller

```java
@RestController
public class UsuarioController {
    private final UsuarioCommandService commandService;
    private final UsuarioQueryService queryService;
    
    @PostMapping  // Write → Command
    public ResponseEntity<UsuarioResponse> criar(@RequestBody UsuarioRequest req) {
        return ResponseEntity.ok(commandService.criarUsuario(req));
    }
    
    @GetMapping  // Read → Query
    public ResponseEntity<List<UsuarioListResponse>> listar() {
        return ResponseEntity.ok(queryService.listarTodosUsuarios());
    }
}
```

## ⚖️ Quando Usar Cada Abordagem?

### 🟢 Use QUERY (Bypass) quando:

- ✅ Busca simples por ID
- ✅ Listagem sem filtros complexos
- ✅ Estatísticas simples (count, sum)
- ✅ Relatórios read-only
- ✅ **NÃO há lógica de negócio**

### 🔴 Use COMMAND (Domain) quando:

- ✅ Criar, Atualizar, Deletar
- ✅ Qualquer operação com **validação**
- ✅ Operações com **regras de negócio**
- ✅ Transações complexas
- ✅ Eventos de domínio

### ⚠️ NUNCA faça Bypass se:

- ❌ Tem validação/regra de negócio
- ❌ Precisa aplicar filtros baseados em lógica
- ❌ Envolve cálculos ou transformações
- ❌ Tem eventos ou side effects

## 🎁 Benefícios

### 1. Performance
```
Query Simples: 2 camadas em vez de 4
Application → Infrastructure (bypass Domain)
```

### 2. Separação Clara
```
Commands = Mudam estado = Domain
Queries = Leem estado = Bypass OK
```

### 3. Escalabilidade
```
Pode ter databases separados:
- Write DB (commands)
- Read DB (queries - otimizado para leitura)
```

### 4. Código Mais Limpo
```java
// Fica claro a intenção
commandService.criar()  // Vai ter lógica
queryService.listar()   // Query simples
```

## 📊 Comparação

| Aspecto | Command | Query |
|---------|---------|-------|
| **Operação** | POST, PUT, DELETE | GET |
| **Passa por Domain?** | ✅ SIM (obrigatório) | ❌ NÃO (bypass) |
| **Tem lógica?** | ✅ Sim | ❌ Não (apenas busca) |
| **Performance** | Normal (4 camadas) | Melhor (2 camadas) |
| **Validação** | ✅ Sim | ❌ Não necessária |
| **Exemplos** | Criar usuário, Deletar | Listar, Buscar por ID |

## 🚦 Regras Práticas

### ✅ PODE fazer Bypass:
```java
// Query simples - busca dados
GET /usuarios → queryService.listarTodos()
GET /usuarios/1 → queryService.buscarPorId(1)
GET /usuarios/count → queryService.contar()
```

### ❌ NÃO PODE fazer Bypass:
```java
// Tem lógica de negócio!
GET /usuarios/ativos → useCase.buscarAtivos() // "ativo" é regra
GET /usuarios/notificaveis → useCase.buscarNotificaveis() // regra
POST /usuarios → commandService.criar() // validação
```

## 📁 Estrutura de Pastas

```
application/
├── rest/
│   ├── UsuarioController.java
│   └── dto/
│       ├── UsuarioRequest.java
│       └── UsuarioResponse.java
└── service/
    ├── command/                          ← Commands (write)
    │   └── UsuarioCommandService.java
    └── query/                            ← Queries (read)
        ├── UsuarioQueryService.java
        └── dto/
            └── UsuarioListResponse.java  ← DTO específico para query
```

## 🎓 Conceitos Relacionados

### CQRS Simples (nosso caso)
- Mesma database
- Separação apenas em código
- Queries podem fazer bypass

### CQRS Avançado
- Databases separados (write DB, read DB)
- Event Sourcing
- Eventual Consistency
- Mais complexo

## 🔍 Exemplos de Endpoints

### Commands (passa por Domain)
```bash
# Criar usuário - TEM validação
POST /api/usuarios
Body: {"nome": "João", "email": "joao@test.com"}

# Atualizar usuário - TEM lógica
PUT /api/usuarios/1
Body: {"nome": "João Silva", "email": "joao@test.com"}

# Deletar usuário - TEM verificação
DELETE /api/usuarios/1
```

### Queries (bypass Domain)
```bash
# Listar todos - Query simples
GET /api/usuarios

# Buscar por ID - Query simples
GET /api/usuarios/1

# Buscar por email - Query simples
GET /api/usuarios/email/joao@test.com

# Contar usuários - Estatística simples
GET /api/usuarios/count
```

## ⚠️ Avisos Importantes

### 1. Não abuse do Bypass!
```java
// ❌ ERRADO - tem lógica!
queryService.buscarUsuariosQuePodemReceberEmail()

// ✅ CORRETO - query simples
queryService.buscarPorId(1)
```

### 2. Se tiver dúvida, use Domain!
```
Dúvida se tem lógica? → Passe por Domain!
Melhor "pecar" por passar por Domain do que fazer bypass indevido.
```

### 3. Documente decisões
```java
/**
 * Busca por ID
 * BYPASS justificado: query simples sem lógica
 */
public Optional<Usuario> buscarPorId(Long id) { }
```

## 📚 Referências

- **Greg Young** - Criador do termo CQRS (2010)
- **Martin Fowler** - CQRS Pattern
- **Eric Evans** - Domain-Driven Design

## 🎯 Resumo

| Conceito | Explicação |
|----------|------------|
| **CQRS** | Separar Commands (write) de Queries (read) |
| **Command** | Operações de escrita que SEMPRE passam por Domain |
| **Query** | Operações de leitura que PODEM fazer bypass |
| **Bypass** | Pular o Domain e ir direto à Infrastructure |
| **Quando Bypass** | Só para queries SIMPLES sem lógica |

---

**Implementado em:** Outubro 2025  
**Padrão:** CQRS Simplificado com Bypass Seletivo

