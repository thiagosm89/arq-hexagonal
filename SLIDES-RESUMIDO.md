# Arquitetura Hexagonal - Apresentação Resumida

## 🎯 Slide 1: Título
```
ARQUITETURA HEXAGONAL
Ports & Adapters Pattern

Spring Boot + Gradle Multi-module
Domain Puro + DDD + CQRS
```

---

## 📐 Slide 2: O Que É?

**Arquitetura Hexagonal (Ports & Adapters)**
- Criada por Alistair Cockburn (2005)
- Isola lógica de negócio de tecnologias
- Domain no centro, frameworks na periferia

**Objetivo:** Domain independente de frameworks

**⚠️ Não confundir:** Clean Architecture e Onion são arquiteturas diferentes (mas com princípios similares)

---

## 🏗️ Slide 3: Estrutura - 3 Módulos

```
APPLICATION
    ↓ conhece
INFRASTRUCTURE
    ↓ conhece
DOMAIN (PURO - sem frameworks!)
```

**Inversão de dependência:** Domain não conhece nada!

---

## 📦 Slide 4: Módulo DOMAIN

```
domain/
├── model/          # Entidades (Usuario)
├── valueobject/    # Email, CPF (imutáveis)
├── usecase/        # Lógica de negócio PURA
└── ports/
    ├── in/        # UsuarioInboundPort
    └── out/       # UsuarioOutboundPort
```

**Características:**
- ❌ SEM Spring, SEM JPA
- ✅ Java puro
- ✅ Protegido por build.gradle

---

## 📦 Slide 5: Módulo INFRASTRUCTURE

```
infrastructure/
└── persistence/
    ├── entity/        # UsuarioEntity (JPA)
    ├── repository/    # Spring Data JPA
    └── adapter/       # Implementa OutboundPort
```

**Função:** Adaptadores de SAÍDA (Driven)
- Implementa tecnologias (JPA, REST clients, etc.)

---

## 📦 Slide 6: Módulo APPLICATION

```
application/
├── rest/              # Controllers (entrada)
├── service/
│   ├── command/      # Write → Domain
│   └── query/        # Read → Bypass
└── config/           # Beans manuais
```

**Função:** Configuração + Adaptadores de ENTRADA (Driving)

---

## 🔌 Slide 7: Portas (Ports)

**InboundPort (Entrada)**
```java
public interface UsuarioInboundPort {
    Usuario criarUsuario(String nome, Email email, CPF cpf);
}
```
- Implementada por: **Domain**
- Chamada por: **Application**

**OutboundPort (Saída)**
```java
public interface UsuarioOutboundPort {
    Usuario salvar(Usuario usuario);
}
```
- Implementada por: **Infrastructure**
- Chamada por: **Domain**

---

## 💎 Slide 8: Value Objects

**Objetos imutáveis e auto-validáveis**

```java
// Email
Email email = Email.of("joao@test.com");
email.getDomain(); // "test.com"

// CPF
CPF cpf = CPF.of("123.456.789-09");
cpf.getFormatted(); // "123.456.789-09"
cpf.getMasked();    // "***.***. 789-09"
```

**Se existe, é válido!**

---

## 🔄 Slide 9: Fluxo Completo

```
HTTP POST → Controller
    ↓
CommandService (converte String → Value Objects)
    ↓
InboundPort (interface)
    ↓
UseCase (Domain - lógica pura)
    ↓
OutboundPort (interface)
    ↓
Adapter (Infrastructure - JPA)
    ↓
Database
```

---

## 🎯 Slide 10: CQRS

**Command Query Responsibility Segregation**

**Commands (Write):**
```
POST/PUT/DELETE → CommandService → Domain
```
Validação + Regras

**Queries (Read):**
```
GET → QueryService → Infrastructure (BYPASS)
```
Performance otimizada

---

## ✅ Slide 11: Decisões Arquiteturais

1. **Use Cases no Domain** (não Application)
2. **Nomenclatura genérica** (InboundPort/OutboundPort)
3. **Controllers no Application** (não Infrastructure)
4. **Domain 100% puro** (build protegido)
5. **CQRS implementado** (Command/Query separados)
6. **Value Objects nas Ports** (type safe)

---

## 🛡️ Slide 12: Proteção do Domain

**Build Gradle:**
```gradle
if (dependency.contains('springframework')) {
    throw Exception("Domain não pode ter Spring!")
}
```

**Testes Arquiteturais:**
```java
@Test
void domainNaoDeveImportarSpring() {
    // Verifica automaticamente
}
```

**Resultado:** Impossível poluir Domain por acidente!

---

## 🎁 Slide 13: Benefícios

**Independência**
- Domain não conhece frameworks
- Portável, reutilizável

**Testabilidade**
- Testes puros, sem Spring
- Rápidos e simples

**Flexibilidade**
- Trocar tecnologias facilmente
- Múltiplas implementações

**Type Safety**
- Value Objects garantem tipos corretos
- Compilador ajuda

---

## 📊 Slide 14: Antes vs Depois

**ANTES (Tradicional):**
```
@Service ← Acoplado ao Spring
public class UsuarioService {
    @Autowired ← Dependência
    private UsuarioRepository repo;
}
```

**DEPOIS (Hexagonal):**
```
// SEM Spring!
public class UsuarioUseCaseImpl {
    private final UsuarioOutboundPort port;
}

// Spring só no Application
@Configuration
public class Config {
    @Bean
    public UsuarioInboundPort bean() { }
}
```

---

## 🎨 Slide 15: Padrões Implementados

```
✅ Arquitetura Hexagonal
✅ Clean Architecture
✅ Domain-Driven Design (DDD)
✅ CQRS
✅ Dependency Inversion
✅ Value Objects
✅ Domain Purity
```

**Resultado:** Arquitetura profissional e moderna!

---

## 🚀 Slide 16: Como Executar

```bash
# Compilar
./gradlew build

# Executar
./gradlew :application:bootRun

# Acessar
http://localhost:8080/api/usuarios
```

**Tecnologias:**
Java 17, Spring Boot 3.2, Gradle, H2, Lombok

---

## 📚 Slide 17: Documentação

**9 arquivos de documentação:**
- README.md
- ARCHITECTURE.md
- CQRS.md
- VALUE-OBJECTS.md
- DOMAIN-PURITY.md
- PORTS-NOMENCLATURE.md
- BEST-PRACTICES-SUMMARY.md
- GUIA-RAPIDO.md
- Mais...

**Projeto completo e documentado!**

---

## 🎯 Slide 18: Conclusão

**Arquitetura Hexagonal permite:**
- ✅ Domain puro e isolado
- ✅ Fácil trocar tecnologias
- ✅ Testes simples e rápidos
- ✅ Código expressivo e type-safe
- ✅ Manutenção facilitada

**Resultado:** Sistema robusto e profissional

---

## 💬 Slide 19: Perguntas?

**GitHub:** github.com/seu-usuario/arq-hexagonal
**Email:** seu@email.com
**LinkedIn:** linkedin.com/in/seu-perfil

---

## 🎉 Slide 20: Obrigado!

**Arquitetura Hexagonal**
**Domain Puro + DDD + CQRS + Value Objects**

Código disponível no GitHub

---

