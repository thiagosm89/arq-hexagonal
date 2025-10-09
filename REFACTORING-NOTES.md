# Notas sobre a Refatoração Arquitetural

## 🎯 Objetivo

Implementar uma **Arquitetura Hexagonal Pura**, onde os casos de uso ficam no módulo Domain (onde devem estar), mantendo-o livre de dependências de frameworks.

## 🔄 Mudanças Realizadas

### Antes (Implementação Inicial)
```
domain/                    ← Só interfaces e entidades
  ├── model/
  ├── ports/
  └── exception/

application/               ← Use cases com @Service
  └── usecase/
      └── UsuarioUseCaseImpl.java  ❌ (@Service, Spring)
```

### Depois (Arquitetura Pura)
```
domain/                    ← Lógica de negócio COMPLETA
  ├── model/
  ├── usecase/             ✅ Use cases PUROS (sem Spring)
  │   └── UsuarioUseCaseImpl.java
  ├── ports/
  └── exception/

application/               ← Só configuração
  └── config/
      └── UseCaseConfiguration.java  ✅ @Configuration manual
```

## 📝 Mudanças Específicas

### 1. Movida Implementação do Use Case
- **De:** `application/usecase/UsuarioUseCaseImpl.java`
- **Para:** `domain/usecase/UsuarioUseCaseImpl.java`
- **Mudança:** Removidas anotações `@Service` e `@Slf4j`

### 2. Criada Configuração Manual
- **Arquivo:** `application/config/UseCaseConfiguration.java`
- **Conteúdo:**
  ```java
  @Configuration
  public class UseCaseConfiguration {
      @Bean
      public UsuarioUseCase usuarioUseCase(UsuarioRepository repository) {
          return new UsuarioUseCaseImpl(repository);  // Instanciação manual
      }
  }
  ```

### 3. Movidos Testes
- **De:** `application/test/.../usecase/UsuarioUseCaseImplTest.java`
- **Para:** `domain/test/.../usecase/UsuarioUseCaseImplTest.java`
- **Mudança:** Instanciação direta com `new` (sem Spring)

### 4. Atualizado domain/build.gradle
- Adicionadas dependências de teste (Mockito) pois agora os testes do use case estão no domain

## ✅ Benefícios Conquistados

1. **Domain 100% Puro**
   - Sem dependências de Spring
   - Sem anotações de frameworks
   - Portável para qualquer ambiente

2. **Casos de Uso no Lugar Certo**
   - Orquestração de negócio = negócio
   - Ficam no Domain, onde devem estar

3. **Testabilidade Máxima**
   - Use cases testados sem Spring
   - Instanciação simples: `new UsuarioUseCaseImpl(mockRepo)`

4. **Spring Boot Funcional**
   - Configuração manual via `@Bean`
   - Injeção de dependências funciona normalmente
   - Controllers recebem use cases via `@Autowired`

## 🏗️ Arquitetura Final

```
                         Usuário (HTTP)
                              ↓
                    UsuarioController (Infrastructure)
                              ↓
                    UsuarioUseCase (interface no Domain)
                              ↓
                    UsuarioUseCaseImpl (PURO no Domain)
                              ↓
                    UsuarioRepository (interface no Domain)
                              ↓
                    UsuarioRepositoryAdapter (Infrastructure)
                              ↓
                         Banco de Dados
```

## 🎓 Princípio Aplicado

> "A orquestração da lógica de negócio faz parte do negócio, portanto deve estar no Domain."

Para manter o Domain puro (sem frameworks), usamos **configuração manual via @Configuration** no módulo Application.

## 📚 Arquivos de Documentação Atualizados

- ✅ README.md - Estrutura de módulos atualizada
- ✅ ARCHITECTURE.md - Diagrama e explicações detalhadas
- ✅ domain/build.gradle - Dependências de teste adicionadas
- ✅ application/config/UseCaseConfiguration.java - Nova classe criada

## 🔍 Como Verificar

1. **Domain puro:**
   ```bash
   # Não deve encontrar nenhuma referência ao Spring no Domain
   grep -r "org.springframework" domain/src/main/java
   # (vazio = sucesso)
   ```

2. **Use cases no Domain:**
   ```bash
   ls domain/src/main/java/com/example/domain/usecase/
   # Deve listar: UsuarioUseCaseImpl.java
   ```

3. **Configuração manual existe:**
   ```bash
   ls application/src/main/java/com/example/application/config/
   # Deve listar: UseCaseConfiguration.java
   ```

## 🚀 Próximos Passos Sugeridos

- [ ] Adicionar mais use cases seguindo o mesmo padrão
- [ ] Considerar usar eventos de domínio (também puros)
- [ ] Implementar value objects no Domain
- [ ] Adicionar aggregates se necessário

---

## 🔄 Refatoração #2: Controllers REST movidos para Application

### Data
Outubro 2025

### Mudança
Movida a camada REST de **Infrastructure** para **Application**

### Motivação
- Controllers REST são **Driving Adapters** (adaptadores primários/de entrada)
- Eles iniciam ações na aplicação, não são recursos de infraestrutura
- Infrastructure deve conter apenas **Driven Adapters** (adaptadores secundários/de saída)

### Arquivos Movidos
- `infrastructure/rest/UsuarioController.java` → `application/rest/UsuarioController.java`
- `infrastructure/rest/dto/UsuarioRequest.java` → `application/rest/dto/UsuarioRequest.java`
- `infrastructure/rest/dto/UsuarioResponse.java` → `application/rest/dto/UsuarioResponse.java`

### Resultado
```
Application:
  - Driving Adapters (REST Controllers, CLI, etc.)
  - Configuração e Bootstrap

Infrastructure:
  - Driven Adapters (Repositórios, APIs externas, etc.)
  - Apenas recursos de infraestrutura técnica

Domain:
  - Lógica de negócio pura
  - Casos de uso
  - Entidades
```

---

## 🎯 Refatoração #3: Implementação de CQRS

### Data
Outubro 2025

### Mudança
Implementado **CQRS** (Command Query Responsibility Segregation) na camada Application

### Estrutura Criada
```
application/service/
├── command/
│   └── UsuarioCommandService.java    # Write operations
└── query/
    ├── UsuarioQueryService.java      # Read operations
    └── dto/
        └── UsuarioListResponse.java
```

### Motivação
- **Separar responsabilidades** entre operações de leitura e escrita
- **Commands** (write) sempre passam por Domain (validações, regras)
- **Queries** (read simples) podem fazer **bypass** (performance)

### Princípios Aplicados
1. **Commands (POST, PUT, DELETE)**
   - Passam por Domain
   - Validações e regras de negócio
   - Garantem integridade
   - Flow: Controller → CommandService → UseCase → Domain

2. **Queries (GET)**
   - Podem fazer bypass do Domain
   - Apenas buscas simples
   - Otimizadas para performance
   - Flow: Controller → QueryService → Infrastructure

### Benefícios
- ✅ Performance melhorada em queries
- ✅ Separação clara de responsabilidades
- ✅ Commands mantêm garantias do Domain
- ✅ Código mais organizado e intencional

### Arquivos Criados
- `UsuarioCommandService.java` - Operações de escrita
- `UsuarioQueryService.java` - Operações de leitura
- `UsuarioListResponse.java` - DTO para queries
- `CQRS.md` - Documentação completa do padrão
- `CQRS-DIAGRAM.md` - Diagramas visuais

### Controller Atualizado
Controller agora usa:
- `commandService` para POST, PUT, DELETE
- `queryService` para GET

### Novos Endpoints
- `GET /api/usuarios/email/{email}` - Buscar por email
- `GET /api/usuarios/count` - Contar usuários
- `PUT /api/usuarios/{id}` - Atualizar usuário

---

## 🎯 Refatoração #4: Nomenclatura Correta de Portas

### Data
Outubro 2025

### Mudança
Renomeado as interfaces de portas para nomenclatura **genérica e agnóstica**

### Renomeações
```
UsuarioUseCase    → UsuarioInboundPort
UsuarioRepository → UsuarioOutboundPort
```

### Motivação
Na Arquitetura Hexagonal **pura**, as portas devem ter nomes genéricos:
- ❌ `UseCase` indica implementação específica (caso de uso)
- ❌ `Repository` indica tecnologia específica (JPA/Database)
- ✅ `InboundPort` é genérico (qualquer entrada: REST, CLI, GraphQL)
- ✅ `OutboundPort` é genérico (qualquer saída: BD, REST, Kafka, Cache)

### Princípio
**Domain deve ser agnóstico de tecnologia**
- Não deve conhecer Spring, JPA, REST, etc.
- Portas são contratos genéricos
- Adaptadores são implementações específicas

### Arquivos Afetados
- `UsuarioInboundPort.java` (nova interface)
- `UsuarioOutboundPort.java` (nova interface)
- `UsuarioUseCaseImpl.java` (implementa InboundPort, usa OutboundPort)
- `UsuarioRepositoryAdapter.java` (implementa OutboundPort)
- `UsuarioCommandService.java` (usa InboundPort)
- `UseCaseConfiguration.java` (configura InboundPort)
- Todos os testes

### Benefícios
- ✅ Domain 100% agnóstico de tecnologia
- ✅ Nomes genéricos e flexíveis
- ✅ Pode ter múltiplas implementações
- ✅ Segue fielmente a Arquitetura Hexagonal

### Documentação
- `PORTS-NOMENCLATURE.md` - Documentação completa sobre nomenclatura

---

## 🛡️ Refatoração #5: Proteção de Pureza do Domain

### Data
Outubro 2025

### Mudança
Implementadas **proteções automáticas** para garantir que o Domain permaneça puro

### Proteções Adicionadas

#### 1. Gradle Build Protection
```gradle
// domain/build.gradle
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

**Resultado:** Se alguém tentar adicionar Spring ao Domain, o **build falhará**!

#### 2. Testes Arquiteturais
```java
// ArchitectureTest.java
@Test
void domainNaoDeveImportarSpring() {
    // Escaneia todos os .java
    // Falha se encontrar imports proibidos
}

@Test
void domainNaoDeveUsarAnotacoesDeFrameworks() {
    // Verifica anotações como @Service, @Entity
    // Falha se encontrar
}
```

### Motivação
- Garantir que Domain permaneça **100% puro**
- Prevenir acidentes (alguém adicionando Spring por engano)
- Tornar a regra **obrigatória** e **automatizada**
- Documentar claramente o que é permitido

### Dependências Bloqueadas
- ❌ `org.springframework.*` (Spring Framework)
- ❌ `jakarta.persistence.*` (JPA)
- ❌ `org.hibernate.*` (Hibernate)

### Arquivos Criados
- `domain/build.gradle` - Proteção de dependências
- `ArchitectureTest.java` - Testes arquiteturais
- `DOMAIN-PURITY.md` - Guia completo de pureza

### Como Verificar
```bash
# Testes automáticos
./gradlew :domain:test --tests ArchitectureTest

# Verificação manual
grep -r "import org.springframework" domain/src/main/java/
```

### Benefícios
- ✅ **Automático** - Detecta violações no build
- ✅ **Documentado** - Regras claras em DOMAIN-PURITY.md
- ✅ **Testado** - Testes garantem cumprimento
- ✅ **Preventivo** - Impossível quebrar por acidente

---

**Data da Refatoração:** Outubro 2025  
**Motivação:** Seguir fielmente os princípios da Arquitetura Hexagonal mantendo Domain puro e PROTEGIDO, separando corretamente adaptadores primários (Application) de secundários (Infrastructure), implementando CQRS para otimização, usando nomenclatura correta e genérica para as portas, e garantindo pureza do Domain através de proteções automáticas

