# 🏗️ Testes Arquiteturais do Domain

Este documento explica os testes arquiteturais que garantem a **pureza do módulo Domain**.

## 🎯 Objetivo

Garantir que o módulo `domain` permanece **puro e independente**, seguindo os princípios da **Arquitetura Hexagonal**.

## 📋 Testes Implementados

### 1. **DomainPurityTest.java**
Teste simples que verifica:
- ✅ JUnit Jupiter está funcionando
- ✅ Domain NÃO tem acesso ao Spring Framework
- ✅ Domain NÃO tem acesso ao JPA/Hibernate
- ✅ Domain TEM acesso ao Java Standard Library
- ✅ Domain TEM acesso à validação básica (Jakarta Validation)

### 2. **ArchUnitTest.java**
Testes arquiteturais robustos usando ArchUnit:

#### 🚫 Regras de PROIBIÇÃO

**Regra 1: Domain não pode importar Spring**
```java
@ArchTest
static final ArchRule domainNaoDeveImportarSpring = 
    noClasses().should().dependOnClassesThat()
    .resideInAPackage("org.springframework..");
```

**Regra 2: Domain não pode importar JPA**
```java
@ArchTest
static final ArchRule domainNaoDeveImportarJPA = 
    noClasses().should().dependOnClassesThat()
    .resideInAPackage("jakarta.persistence..");
```

**Regra 3: Domain não pode usar anotações de frameworks**
```java
@ArchTest
static final ArchRule domainNaoDeveUsarAnotacoesDeFrameworks = 
    noClasses().should().beAnnotatedWith("org.springframework.stereotype.Service");
```

**Regra 4: Domain não pode depender de outros módulos**
```java
@ArchTest
static final ArchRule domainNaoDeveDependerDeOutrosModulos = 
    noClasses().should().dependOnClassesThat()
    .resideInAPackage("com.example.application..");
```

#### ✅ Regras de ORGANIZAÇÃO

**Regra 5: Não deve haver dependências circulares**
```java
@ArchTest
static final ArchRule naoDeveHaverDependenciasCirculares = 
    slices().matching("com.example.domain.(*)..")
    .should().beFreeOfCycles();
```

**Regra 6: Classes devem ser POJOs**
```java
@ArchTest
static final ArchRule classesDevemSerPOJOs = 
    noClasses().should().beAssignableTo("org.springframework.stereotype.Component");
```

---

## 🚀 Como Executar

### Executar todos os testes do Domain:
```bash
./gradlew :domain:test
```

### Executar apenas testes arquiteturais:
```bash
./gradlew :domain:test --tests "*ArchUnitTest"
./gradlew :domain:test --tests "*DomainPurityTest"
```

### Executar apenas um teste específico:
```bash
./gradlew :domain:test --tests "ArchUnitTest.domainNaoDeveImportarSpring"
```

---

## 📊 Dependências Permitidas vs Proibidas

### ✅ **PERMITIDAS no Domain:**
```gradle
// Java Standard Library
java.*

// Validação básica
jakarta.validation.*

// Próprias classes do Domain
com.example.domain.*
```

### ❌ **PROIBIDAS no Domain:**
```gradle
// Frameworks
org.springframework.*
org.hibernate.*
jakarta.persistence.*
javax.persistence.*

// Outros módulos do projeto
com.example.application.*
com.example.infrastructure.*
```

---

## 🔍 O Que Os Testes Verificam

### 1. **Dependências de Build**
- Verifica se `build.gradle` não tem dependências proibidas
- Bloqueia automaticamente Spring/JPA no Domain

### 2. **Imports de Código**
- Verifica se classes não importam frameworks
- Garante que só usa Java puro + validação básica

### 3. **Anotações**
- Verifica se não usa anotações de frameworks
- Garante que é POJO puro

### 4. **Organização**
- Verifica estrutura de pacotes
- Garante que não há dependências circulares

---

## ⚠️ Quando Os Testes Falham

### Exemplo de Erro:
```
❌ VIOLAÇÃO ARQUITETURAL DETECTADA!

O módulo Domain não pode importar frameworks!
Domain deve ser PURO e independente.

Violações encontradas:
  - Usuario.java:5 - Import proibido: import org.springframework.stereotype.Service
```

### Como Corrigir:
1. **Remover imports proibidos**
2. **Remover anotações de frameworks**
3. **Mover lógica para módulo correto**
4. **Usar interfaces/portas adequadas**

---

## 🎯 Benefícios

### ✅ **Garantia de Qualidade**
- Domain sempre puro
- Arquitetura hexagonal respeitada
- Fácil manutenção e teste

### ✅ **Detecção Precoce**
- Falhas detectadas no build
- Não permite regressões arquiteturais
- Documentação viva das regras

### ✅ **Confiabilidade**
- Testes automatizados
- Regras claras e objetivas
- Feedback imediato

---

## 📚 Referências

- [ArchUnit Documentation](https://www.archunit.org/)
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [Domain-Driven Design](https://martinfowler.com/bliki/DomainDrivenDesign.html)

---

## 🔧 Manutenção

### Adicionar Nova Regra:
1. Adicionar `@ArchTest` no `ArchUnitTest.java`
2. Documentar a regra neste arquivo
3. Testar a regra
4. Commit com explicação

### Modificar Regra Existente:
1. Atualizar teste no `ArchUnitTest.java`
2. Atualizar documentação
3. Verificar se não quebra código existente
4. Testar thoroughly

---

**🎉 Com estes testes, o Domain sempre permanecerá puro e a arquitetura hexagonal será respeitada!**
