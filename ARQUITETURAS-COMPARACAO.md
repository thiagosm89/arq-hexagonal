# Comparação: Hexagonal vs Clean vs Onion

## ⚠️ Esclarecimento Importante

**Estas são 3 arquiteturas DIFERENTES**, não sinônimos!

Embora compartilhem princípios similares (Domain no centro, inversão de dependência), elas têm diferenças importantes.

## 📚 Origens

### Hexagonal Architecture
- **Ano:** 2005
- **Autor:** Alistair Cockburn
- **Nome alternativo:** Ports and Adapters
- **Artigo:** "Hexagonal Architecture" (alistair.cockburn.us)

### Onion Architecture
- **Ano:** 2008
- **Autor:** Jeffrey Palermo
- **Artigo:** "The Onion Architecture" (jeffreypalermo.com)

### Clean Architecture
- **Ano:** 2012
- **Autor:** Robert C. Martin (Uncle Bob)
- **Livro:** "Clean Architecture: A Craftsman's Guide" (2017)

---

## 🎨 Representações Visuais

### Hexagonal (Hexágono)
```
        [Adapter]
       /         \
    [Port]     [Port]
      |           |
   [Domain Core]
      |           |
   [Port]      [Port]
       \         /
        [Adapter]
```
**Foco:** Simetria - múltiplos lados/portas iguais

---

### Onion (Cebola - Camadas)
```
┌─────────────────────┐
│  Infrastructure     │ ← Camada 4
│  ┌───────────────┐  │
│  │  Application  │  │ ← Camada 3
│  │  ┌─────────┐  │  │
│  │  │ Domain  │  │  │ ← Camada 2
│  │  │ ┌─────┐ │  │  │
│  │  │ │Core │ │  │  │ ← Camada 1 (centro)
│  │  │ └─────┘ │  │  │
│  │  └─────────┘  │  │
│  └───────────────┘  │
└─────────────────────┘
```
**Foco:** Camadas concêntricas

---

### Clean (Círculos)
```
┌─────────────────────────────┐
│ Frameworks & Drivers        │ ← Círculo 4 (UI, DB)
│  ┌──────────────────────┐   │
│  │ Interface Adapters  │   │ ← Círculo 3 (Controllers)
│  │  ┌──────────────┐   │   │
│  │  │  Use Cases  │   │   │ ← Círculo 2 (Aplicação)
│  │  │  ┌───────┐  │   │   │
│  │  │  │Entities│  │   │   │ ← Círculo 1 (Negócio)
│  │  │  └───────┘  │   │   │
│  │  └──────────────┘   │   │
│  └──────────────────────┘   │
└─────────────────────────────┘
```
**Foco:** Regra de dependência + Use Cases

---

## 📊 Comparação Detalhada

### Terminologia

| Conceito | Hexagonal | Onion | Clean |
|----------|-----------|-------|-------|
| **Núcleo** | Domain | Core Domain | Entities |
| **Lógica App** | Domain | Domain Services | Use Cases |
| **Interface** | Port | Interface | Gateway |
| **Implementação** | Adapter | Infrastructure | Framework |

---

### Estrutura

| Aspecto | Hexagonal | Onion | Clean |
|---------|-----------|-------|-------|
| **Camadas** | 2-3 flexível | 4 fixas | 4 fixas |
| **Forma visual** | Hexágono | Cebola | Círculos |
| **Termo chave** | Port/Adapter | Layer | Circle |
| **Ênfase** | Adaptadores | Camadas | Use Cases |

---

### Foco Principal

**Hexagonal:**
- Ports (interfaces)
- Adapters (implementações)
- Simetria (vários adapters)

**Onion:**
- Camadas concêntricas
- Dependências sempre para dentro
- Camadas bem definidas

**Clean:**
- Use Cases como centro
- Regra de dependência estrita
- Entities vs Use Cases separados

---

## 🔄 Onde Diferem

### 1. Organização de Use Cases

**Hexagonal:**
```
Domain contém tudo junto
- Entities
- Use Cases
- Ports
```

**Clean:**
```
Entities (camada 1)
Use Cases (camada 2) ← Separado!
```

**Onion:**
```
Core (Entities)
Domain Services (Use Cases)
Application Services
```

---

### 2. Nomenclatura

**Hexagonal:**
- Port (interface)
- Adapter (implementação)
- Driving/Driven

**Onion:**
- Core
- Domain Services
- Infrastructure Services

**Clean:**
- Entities
- Use Cases (Interactors)
- Interface Adapters
- Frameworks & Drivers

---

### 3. Representação Visual

**Hexagonal:** Forma de hexágono (6 lados)
- Não importa quantos lados
- Múltiplos adapters simétricos

**Onion:** Camadas de cebola
- 4 camadas específicas
- Cada camada tem papel definido

**Clean:** Círculos concêntricos
- 4 círculos específicos
- Regra de dependência entre círculos

---

## 🤝 O Que Têm em Comum

### Princípios Compartilhados:

1. **Domain no centro**
2. **Inversão de dependência**
3. **Isolamento de frameworks**
4. **Interfaces como boundaries**
5. **Testabilidade**

**Por isso a confusão!** Mas são arquiteturas distintas.

---

## 📖 Referências Originais

### Hexagonal Architecture
- **Artigo:** [alistair.cockburn.us/hexagonal-architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- **Ano:** 2005
- **Foco:** Ports and Adapters

### Onion Architecture
- **Artigo:** [jeffreypalermo.com/2008/07/the-onion-architecture-part-1](https://jeffreypalermo.com/2008/07/the-onion-architecture-part-1/)
- **Ano:** 2008
- **Foco:** Layered approach

### Clean Architecture
- **Livro:** "Clean Architecture" - Robert C. Martin
- **Ano:** 2012 (conceito), 2017 (livro)
- **Blog:** [blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

---

## 🎯 Este Projeto

### É **Hexagonal Architecture** com:
- ✅ Ports & Adapters (terminologia Hexagonal)
- ✅ InboundPort/OutboundPort (Hexagonal)
- ✅ Estrutura modular (3 módulos)

### Influências de:
- **Clean:** Use Cases no Domain, Domain puro
- **DDD:** Value Objects, Entidades
- **CQRS:** Command/Query separation

### NÃO é:
- ❌ Onion Architecture pura
- ❌ Clean Architecture pura

---

## 💡 Por Que Existem as 3?

### Evoluções Históricas

**2005 - Hexagonal (Cockburn)**
→ Revolucionou: Ports & Adapters

**2008 - Onion (Palermo)**
→ Refinamento: Camadas mais claras

**2012 - Clean (Uncle Bob)**
→ Consolidação: Regras + Use Cases + Diagrama famoso

Cada uma trouxe contribuições únicas!

---

## 📝 Correção nos Slides

Vou corrigir agora os slides para remover a afirmação incorreta:

<function_calls>
<invoke name="search_replace">
<parameter name="file_path">SLIDES-ARQUITETURA-HEXAGONAL.md
