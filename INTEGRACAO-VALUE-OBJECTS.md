# Integração de Value Objects com Entidades

## 🎯 Resumo da Implementação

A entidade `Usuario` agora usa Value Objects (`Email` e `CPF`) em vez de Strings primitivas.

## 📊 Fluxo Completo

```
┌─────────────────────────────────────────────────────────┐
│                     DOMAIN                              │
│                                                         │
│  Usuario (Entidade)                                     │
│    - Long id                                            │
│    - String nome                                        │
│    - Email email    ← Value Object (imutável)          │
│    - CPF cpf        ← Value Object (imutável, opcional)│
│                                                         │
│  Email (Value Object)                                   │
│    - String value   ← sempre válido                     │
│                                                         │
│  CPF (Value Object)                                     │
│    - String value   ← sempre válido                     │
└─────────────────────────────────────────────────────────┘
                        ↕
              (Conversão no Adapter)
                        ↕
┌─────────────────────────────────────────────────────────┐
│                  INFRASTRUCTURE                         │
│                                                         │
│  UsuarioEntity (JPA)                                    │
│    - Long id                                            │
│    - String nome                                        │
│    - String email   ← String no banco                   │
│    - String cpf     ← String no banco                   │
│                                                         │
│  UsuarioRepositoryAdapter                               │
│    - toDomain():   String → Value Object                │
│    - toEntity():   Value Object → String                │
└─────────────────────────────────────────────────────────┘
```

## 🔄 Conversão entre Camadas

### Domain → Infrastructure (Salvar)

```java
// DOMAIN (Value Objects)
Usuario usuario = Usuario.criar("João", "joao@test.com", "123.456.789-09");
Email email = usuario.getEmail();  // Value Object
CPF cpf = usuario.getCpf();        // Value Object

    ↓ toEntity()

// INFRASTRUCTURE (Strings)
UsuarioEntity entity = new UsuarioEntity(
    id,
    "João",
    "joao@test.com",    // String
    "12345678909"       // String
);
```

### Infrastructure → Domain (Buscar)

```java
// INFRASTRUCTURE (Strings do banco)
UsuarioEntity entity = jpaRepository.findById(1);
String email = "joao@test.com";    // String
String cpf = "12345678909";         // String

    ↓ toDomain()

// DOMAIN (Value Objects)
Usuario usuario = Usuario.criar(nome, email, cpf);
Email emailVO = usuario.getEmail();  // Value Object (validado)
CPF cpfVO = usuario.getCpf();        // Value Object (validado)
```

## 💡 Uso da Entidade Usuario

### Criação com Value Objects

```java
// Opção 1: Diretamente com Value Objects
Email email = Email.of("joao@example.com");
CPF cpf = CPF.of("123.456.789-09");
Usuario usuario = new Usuario("João Silva", email, cpf);

// Opção 2: Factory method (mais simples)
Usuario usuario = Usuario.criar("João Silva", "joao@example.com", "123.456.789-09");

// Opção 3: Sem CPF
Usuario usuario = Usuario.criar("João Silva", "joao@example.com");
```

### Acessando Value Objects

```java
// Obter Value Objects
Email email = usuario.getEmail();
CPF cpf = usuario.getCpf();

// Usar métodos dos Value Objects
String domain = email.getDomain();           // "example.com"
String cpfFormatado = cpf.getFormatted();    // "123.456.789-09"

// Obter como String (para compatibilidade)
String emailStr = usuario.getEmailAsString();  // "joao@example.com"
String cpfStr = usuario.getCpfAsString();      // "12345678909"
```

### Atualizando Value Objects

```java
// Value Objects são IMUTÁVEIS
// Para "mudar", criamos um novo

// Atualizar email
usuario.atualizarEmail("novo@example.com");
// ou
usuario.atualizarEmail(Email.of("novo@example.com"));

// Atualizar CPF
usuario.atualizarCpf("111.444.777-35");
// ou
usuario.atualizarCpf(CPF.of("111.444.777-35"));
```

### Verificando CPF

```java
// CPF é opcional
if (usuario.temCpf()) {
    CPF cpf = usuario.getCpf();
    System.out.println(cpf.getFormatted());
}
```

## ✅ Benefícios da Integração

### 1. Validação Automática

```java
// ❌ Antes: String aceita qualquer valor
Usuario usuario = new Usuario("João", "email-invalido"); // Aceita!

// ✅ Agora: Value Object valida automaticamente
Usuario usuario = Usuario.criar("João", "email-invalido"); // Exceção!
```

### 2. Type Safety

```java
// ❌ Antes: Pode confundir
public void criar(String nome, String email, String cpf) { }
criar(nome, cpf, email); // Ops! Invertido!

// ✅ Agora: Impossível confundir
public void criar(String nome, Email email, CPF cpf) { }
criar(nome, cpf, email); // Erro de compilação!
```

### 3. Comportamento Encapsulado

```java
// ❌ Antes: Lógica espalhada
String domain = email.substring(email.indexOf('@') + 1);

// ✅ Agora: Comportamento no Value Object
String domain = usuario.getEmail().getDomain();
```

### 4. Impossível Ter Valor Inválido

```java
// Se Email ou CPF existem, são VÁLIDOS!
Email email = usuario.getEmail();  // Sempre válido
CPF cpf = usuario.getCpf();        // Sempre válido ou null
```

## 🏗️ Exemplo Completo de Uso

```java
// 1. Criar usuário
Usuario usuario = Usuario.criar(
    "João Silva",
    "joao@example.com",
    "123.456.789-09"
);

// 2. Verificar dados
System.out.println("Nome: " + usuario.getNome());
System.out.println("Email: " + usuario.getEmail().getValue());
System.out.println("Domínio: " + usuario.getEmail().getDomain());

if (usuario.temCpf()) {
    System.out.println("CPF: " + usuario.getCpf().getFormatted());
    System.out.println("CPF Mascarado: " + usuario.getCpf().getMasked());
}

// 3. Atualizar email
usuario.atualizarEmail("joao.novo@example.com");

// 4. Salvar (conversão automática para String)
usuarioRepository.salvar(usuario);
// No banco: email = "joao.novo@example.com" (String)

// 5. Buscar (conversão automática para Value Object)
Usuario buscado = usuarioRepository.buscarPorId(1);
// No Domain: Email (Value Object validado)
```

## 🎓 Conceitos Importantes

### Entidade vs Value Object

| Aspecto | Usuario (Entidade) | Email/CPF (Value Object) |
|---------|-------------------|-------------------------|
| Identidade | Tem ID | Sem identidade |
| Mutabilidade | Pode mudar | Imutável |
| Igualdade | Por ID | Por valor |
| Validação | Pode ter estados inválidos | Sempre válido se existe |

### Onde Fica Cada Coisa

```
Domain:
  - Usuario (Entidade) com Email e CPF (Value Objects)
  - Lógica: validação, regras de negócio
  - Tipo: Email, CPF (objetos)

Infrastructure:
  - UsuarioEntity (JPA) com email e cpf (Strings)
  - Lógica: conversão entre Domain e banco
  - Tipo: String (texto no banco)

Application:
  - UsuarioCommandService usa Usuario do Domain
  - Recebe Strings, converte para Value Objects
  - Retorna DTOs
```

## 📝 Checklist de Implementação

- [x] Value Objects criados (Email, CPF)
- [x] Value Objects com validação
- [x] Value Objects imutáveis
- [x] Value Objects com testes completos
- [x] Usuario usa Value Objects
- [x] Factory methods em Usuario
- [x] Métodos de atualização em Usuario
- [x] UsuarioEntity com Strings
- [x] Adapter converte entre Value Objects e Strings
- [x] Testes atualizados
- [x] Use Cases atualizados

## 🎯 Resumo

**Domain (Puro):**
```java
Usuario → Email (Value Object) + CPF (Value Object)
```

**Infrastructure (JPA):**
```java
UsuarioEntity → String email + String cpf
```

**Conversão:**
```java
Adapter:
  Domain → Infrastructure: Value Object.getValue() → String
  Infrastructure → Domain: String → ValueObject.of(String)
```

**Benefício:**
- Domain trabalha com objetos ricos e validados
- Banco armazena strings simples
- Conversão transparente no Adapter
- Impossível ter valores inválidos no Domain

---

**Implementado em:** Outubro 2025  
**Arquitetura:** Hexagonal com DDD (Value Objects)

