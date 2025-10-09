# Diagrama Visual CQRS

## 🎯 Arquitetura Completa com CQRS

```
┌─────────────────────────────────────────────────────────────────────┐
│                          CLIENT (HTTP)                              │
└──────────────────┬──────────────────────────────────┬───────────────┘
                   │                                  │
        Commands (Write)                   Queries (Read)
    POST, PUT, DELETE                          GET
                   │                                  │
                   ↓                                  ↓
┌──────────────────────────────────┐  ┌──────────────────────────────┐
│      UsuarioController           │  │    UsuarioController         │
│         (Application)            │  │      (Application)           │
└──────────────────┬───────────────┘  └───────────────┬──────────────┘
                   │                                  │
                   ↓                                  ↓
┌──────────────────────────────────┐  ┌──────────────────────────────┐
│   UsuarioCommandService          │  │   UsuarioQueryService        │
│      (Application)               │  │      (Application)           │
│                                  │  │                              │
│  ✅ Commands SEMPRE passam       │  │  ⚡ Queries PODEM fazer      │
│     por Domain                   │  │     BYPASS                   │
└──────────────────┬───────────────┘  └───────────────┬──────────────┘
                   │                                  │
                   │ Passa por Domain                 │ BYPASS!
                   ↓                                  │ (pula Domain)
┌──────────────────────────────────┐                 │
│        UsuarioUseCase            │                 │
│          (Domain)                │                 │
│                                  │                 │
│  - Validações                    │                 │
│  - Regras de negócio             │                 │
│  - Lógica pura                   │                 │
└──────────────────┬───────────────┘                 │
                   │                                  │
                   ↓                                  │
┌──────────────────────────────────┐                 │
│    UsuarioUseCaseImpl            │                 │
│          (Domain)                │                 │
└──────────────────┬───────────────┘                 │
                   │                                  │
                   ↓                                  │
┌──────────────────────────────────┐                 │
│    UsuarioRepository             │                 │
│        (Domain - Port OUT)       │                 │
└──────────────────┬───────────────┘                 │
                   │                                  │
                   ↓                                  ↓
┌─────────────────────────────────────────────────────────────────────┐
│                      INFRASTRUCTURE                                 │
│                                                                     │
│  ┌────────────────────────────────┐  ┌────────────────────────────┐│
│  │  UsuarioRepositoryAdapter      │  │  UsuarioJpaRepository      ││
│  │     (Driven Adapter)           │  │    (Spring Data)           ││
│  └────────────────┬───────────────┘  └────────────┬───────────────┘│
│                   │                                │                │
│                   └────────────┬───────────────────┘                │
└────────────────────────────────┼────────────────────────────────────┘
                                 ↓
                        ┌────────────────┐
                        │   DATABASE     │
                        │     (H2)       │
                        └────────────────┘
```

## 📊 Comparação de Fluxos

### ✍️ Command: POST /api/usuarios (Criar Usuário)

```
1. HTTP Request
       ↓
2. UsuarioController
       ↓
3. UsuarioCommandService ←─────┐
       ↓                       │
4. UsuarioUseCase (interface)  │ Application Layer
       ↓                       │
5. UsuarioUseCaseImpl ←────────┘ Domain Layer (LÓGICA!)
       ↓
   - Valida dados
   - Verifica email duplicado
   - Aplica regras de negócio
       ↓
6. UsuarioRepository (interface)
       ↓
7. UsuarioRepositoryAdapter ←── Infrastructure Layer
       ↓
8. Database

Total: 8 passos (completo e seguro)
```

### 📖 Query: GET /api/usuarios (Listar)

```
1. HTTP Request
       ↓
2. UsuarioController
       ↓
3. UsuarioQueryService ←───────┐
       ↓                       │ Application Layer
4. UsuarioJpaRepository ←──────┘ Infrastructure Layer (DIRETO!)
       ↓
5. Database

Total: 5 passos (rápido e eficiente)
Domain foi IGNORADO (bypass)
```

## 🎨 Separação Visual

```
╔═══════════════════════════════════╗
║          COMMANDS                 ║  Operações de ESCRITA
║                                   ║
║  POST   /api/usuarios             ║  ✅ Passa por Domain
║  PUT    /api/usuarios/{id}        ║  ✅ Validações
║  DELETE /api/usuarios/{id}        ║  ✅ Regras de negócio
║                                   ║
║  CommandService → Domain          ║
╚═══════════════════════════════════╝

╔═══════════════════════════════════╗
║          QUERIES                  ║  Operações de LEITURA
║                                   ║
║  GET /api/usuarios                ║  ⚡ Bypass Domain
║  GET /api/usuarios/{id}           ║  ⚡ Mais rápido
║  GET /api/usuarios/email/{email}  ║  ⚡ Menos camadas
║  GET /api/usuarios/count          ║  ⚡ Performance
║                                   ║
║  QueryService → Infrastructure    ║
╚═══════════════════════════════════╝
```

## 🔄 Fluxo Detalhado

### Command Flow (Write)

```
HTTP POST /api/usuarios
     ↓
┌─────────────────────────────────────────┐
│ UsuarioController                       │
│ - Recebe UsuarioRequest                 │
│ - Delega para CommandService            │
└─────────────┬───────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│ UsuarioCommandService                   │
│ - Chama UseCase do Domain               │
└─────────────┬───────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│ UsuarioUseCaseImpl (Domain)             │
│ ✅ Valida: usuario.isValid()            │
│ ✅ Verifica: email não existe            │
│ ✅ Aplica: regras de negócio             │
│ - Chama Repository                      │
└─────────────┬───────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│ UsuarioRepositoryAdapter (Infrastructure)│
│ - Converte Domain → Entity               │
│ - Chama JPA Repository                   │
└─────────────┬───────────────────────────┘
              ↓
           DATABASE
```

### Query Flow (Read)

```
HTTP GET /api/usuarios
     ↓
┌─────────────────────────────────────────┐
│ UsuarioController                       │
│ - Delega para QueryService              │
└─────────────┬───────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│ UsuarioQueryService                     │
│ ⚡ BYPASS - Pula Domain                 │
│ - Vai direto ao JPA Repository          │
└─────────────┬───────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│ UsuarioJpaRepository (Infrastructure)   │
│ - findAll(), findById(), etc.           │
└─────────────┬───────────────────────────┘
              ↓
           DATABASE

🚀 MAIS RÁPIDO: menos camadas!
```

## 📈 Performance Comparison

```
Command (Write):
Controller → CommandService → UseCase → UseCaseImpl → Repository → Adapter → DB
[1]        [2]              [3]       [4]           [5]          [6]       [7]
7 camadas - Necessário para garantir integridade

Query (Read):
Controller → QueryService → JpaRepository → DB
[1]        [2]           [3]             [4]
4 camadas - Otimizado para performance
```

## 🎯 Quando Usar Cada Um?

### ✅ Use Command Service:

```java
// Tem lógica, validação ou muda estado
commandService.criarUsuario(request);      // Validação + regras
commandService.atualizarUsuario(id, req);  // Lógica de update
commandService.removerUsuario(id);         // Verificações
```

### ✅ Use Query Service:

```java
// Busca simples, sem lógica
queryService.listarTodos();           // SELECT *
queryService.buscarPorId(1);          // SELECT WHERE id
queryService.buscarPorEmail(email);   // SELECT WHERE email
queryService.contarUsuarios();        // COUNT(*)
```

## 🏆 Benefícios Visuais

```
        ANTES (sem CQRS)              │         DEPOIS (com CQRS)
                                      │
    Tudo passa por Domain             │    Commands → Domain
         ↓                            │    Queries → Bypass
    Mais lento para queries           │         ↓
    Mistura responsabilidades         │    Performance melhor
                                      │    Separação clara
```

## 📚 Legenda

- ✅ = Passa por Domain (seguro, com validações)
- ⚡ = Bypass Domain (rápido, otimizado)
- 🔒 = Commands (Write) sempre passam por Domain
- 🚀 = Queries (Read) podem fazer bypass

