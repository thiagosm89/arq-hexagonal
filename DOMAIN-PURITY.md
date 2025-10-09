# Pureza do Módulo Domain

## 🎯 Princípio Fundamental

O módulo **Domain** deve ser **100% puro** e **independente de frameworks**.

## ⚠️ REGRAS OBRIGATÓRIAS

### ❌ O que NÃO pode no Domain:

#### 1. Sem Spring
```java
// ❌ PROIBIDO
import org.springframework.*
@Service
@Component
@Repository
@Autowired
@Inject
```

#### 2. Sem JPA/Hibernate
```java
// ❌ PROIBIDO
import jakarta.persistence.*
import org.hibernate.*
@Entity
@Table
@Column
@Id
```

#### 3. Sem Frameworks Web
```java
// ❌ PROIBIDO
import org.springframework.web.*
@Controller
@RestController
@RequestMapping
```

### ✅ O que PODE no Domain:

#### 1. Java Puro
```java
// ✅ PERMITIDO
import java.util.*
import java.time.*
public class Usuario {
    private String nome;
    // Construtores, getters, setters puros
}
```

#### 2. Bibliotecas Essenciais
```java
// ✅ PERMITIDO
import jakarta.validation.* // Apenas a API, não a implementação
```

#### 3. Lombok (opcional)
```java
// ✅ PERMITIDO (gera código Java puro)
import lombok.*
@Data
@NoArgsConstructor
```

## 🛡️ Mecanismos de Proteção

### 1. Gradle Build Configuration

O `domain/build.gradle` está configurado para **BLOQUEAR** dependências proibidas:

```gradle
configurations.all {
    resolutionStrategy {
        eachDependency { details ->
            if (details.requested.group.startsWith('org.springframework')) {
                throw new GradleException("❌ Domain não pode ter Spring!")
            }
        }
    }
}
```

**Se tentar adicionar Spring ao Domain, o build FALHARÁ! 🚫**

### 2. Testes Arquiteturais

`ArchitectureTest.java` verifica automaticamente:

```java
@Test
void domainNaoDeveImportarSpring() {
    // Escaneia todos os arquivos .java
    // Falha se encontrar imports proibidos
}

@Test
void domainNaoDeveUsarAnotacoesDeFrameworks() {
    // Verifica se há @Service, @Entity, etc.
    // Falha se encontrar anotações proibidas
}
```

Execute: `./gradlew :domain:test`

### 3. Revisão de Código

Checklist para PRs:
- [ ] Nenhum import de `org.springframework.*`
- [ ] Nenhum import de `jakarta.persistence.*`
- [ ] Nenhuma anotação `@Service`, `@Entity`, etc.
- [ ] Apenas Java puro e bibliotecas essenciais

## 📊 Estrutura do Domain

```
domain/
├── model/              ✅ Entidades POJO puras
├── usecase/            ✅ Lógica de negócio pura
├── ports/
│   ├── in/            ✅ Interfaces puras
│   └── out/           ✅ Interfaces puras
└── exception/         ✅ Exceções Java puras
```

## 🔍 Como Verificar

### Verificação Manual

```bash
# Verificar imports proibidos
grep -r "import org.springframework" domain/src/main/java/
grep -r "import jakarta.persistence" domain/src/main/java/

# Deve retornar vazio!
```

### Verificação Automatizada

```bash
# Executar testes arquiteturais
./gradlew :domain:test --tests ArchitectureTest
```

### Análise de Dependências

```bash
# Ver dependências do Domain
./gradlew :domain:dependencies

# Não deve aparecer Spring, JPA, etc.
```

## 💡 Por Quê?

### 1. Independência
```
Domain não depende de nada
  ↓
Pode ser usado em qualquer contexto
  ↓
Web, CLI, Desktop, Mobile
```

### 2. Testabilidade
```java
// Testes puros, sem Spring Test
@Test
void teste() {
    var useCase = new UsuarioUseCaseImpl(mockPort);
    var resultado = useCase.criarUsuario("João", "joao@test.com");
    assertNotNull(resultado);
}
// Rápido, simples, sem container
```

### 3. Portabilidade
```
Domain pode ser movido para:
- Outro projeto
- Outra linguagem (Kotlin, Scala)
- Outro framework
SEM MUDANÇAS!
```

### 4. Clareza
```java
// Código puro revela intenção
public class UsuarioUseCaseImpl {
    // Sem @Service, @Autowired
    // Apenas lógica de negócio
}
```

## 🚨 Exemplos de Violação

### ❌ ERRADO - Domain com Spring

```java
package com.example.domain.usecase;

import org.springframework.stereotype.Service; // ❌ PROIBIDO!
import com.example.domain.ports.in.UsuarioInboundPort;

@Service // ❌ PROIBIDO!
public class UsuarioUseCaseImpl implements UsuarioInboundPort {
    // ...
}
```

**Erro:** Domain acoplado ao Spring!

### ✅ CORRETO - Domain puro

```java
package com.example.domain.usecase;

import com.example.domain.ports.in.UsuarioInboundPort;

// ✅ Sem anotações de framework
public class UsuarioUseCaseImpl implements UsuarioInboundPort {
    
    private final UsuarioOutboundPort outboundPort;
    
    // ✅ Construtor simples
    public UsuarioUseCaseImpl(UsuarioOutboundPort outboundPort) {
        this.outboundPort = outboundPort;
    }
    
    // ✅ Lógica pura
}
```

**Correto:** Java puro, sem frameworks!

## 🔧 Como Adicionar Dependências

### Se REALMENTE precisar de uma biblioteca:

1. **Questione**: Realmente preciso disso no Domain?
2. **Avalie**: É uma biblioteca essencial ou um framework?
3. **Adicione apenas APIs**, nunca implementações

```gradle
// ✅ OK - Apenas a API
implementation 'jakarta.validation:jakarta.validation-api'

// ❌ ERRADO - Implementação
implementation 'org.hibernate.validator:hibernate-validator'
```

## 📚 Referências

- **Clean Architecture** (Uncle Bob): "Frameworks são detalhes"
- **Hexagonal Architecture** (Alistair Cockburn): "Domain isolado"
- **DDD** (Eric Evans): "Foco no domínio, não na tecnologia"

## ✅ Checklist de Pureza

Domain está puro se:

- [ ] ✅ Nenhum import de Spring
- [ ] ✅ Nenhum import de JPA
- [ ] ✅ Nenhuma anotação de framework
- [ ] ✅ Apenas Java puro
- [ ] ✅ Testes sem Spring Test
- [ ] ✅ Build gradle limpo
- [ ] ✅ Testes arquiteturais passando

## 🎯 Lembre-se

> **"O Domain não deve saber que Spring existe!"**

Se você está importando algo do Spring no Domain, **PARE** e refatore!

---

**Mantido por:** Arquitetura do Projeto  
**Última Atualização:** Outubro 2025

