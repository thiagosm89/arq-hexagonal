# Value Objects - Objetos de Valor

## 🎯 O que são Value Objects?

**Value Objects** são objetos **imutáveis** que representam conceitos do domínio definidos pelo seu **valor**, não por identidade.

## 📊 Comparação

### Entidade vs Value Object

| Aspecto | Entidade | Value Object |
|---------|----------|--------------|
| **Identidade** | Tem ID único | Sem identidade |
| **Mutabilidade** | Pode mudar | Imutável |
| **Igualdade** | Por ID | Por valor |
| **Exemplo** | Usuario, Pedido | Email, CPF, Dinheiro |

### Exemplo Prático

```java
// ❌ String primitiva (sem validação)
String email = "email-invalido"; // Aceita qualquer coisa!

// ✅ Value Object (auto-validável)
Email email = Email.of("email-invalido"); // Lança exceção!
Email email = Email.of("joao@example.com"); // OK
```

## 🏗️ Características de um Value Object

### 1. Imutabilidade
```java
public final class Email {
    private final String value; // final - não muda
    
    // SEM setters!
    // public void setValue(String value) { } ❌
}
```

### 2. Validação no Construtor
```java
public static Email of(String email) {
    if (!isValid(email)) {
        throw new IllegalArgumentException("Email inválido");
    }
    return new Email(email);
}
```

### 3. Equals por Valor
```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Email email = (Email) o;
    return Objects.equals(value, email.value); // Compara valor
}
```

### 4. Factory Method
```java
// Construtor privado
private Email(String value) {
    this.value = value;
}

// Factory público
public static Email of(String email) {
    // Valida e cria
}
```

## 💡 Exemplos Implementados

### Email Value Object

```java
// Criação
Email email = Email.of("joao@example.com");

// Métodos úteis
email.getValue();           // "joao@example.com"
email.getDomain();          // "example.com"
email.getLocalPart();       // "joao"
email.isFromDomain("example.com"); // true

// Imutável
Email email2 = Email.of("maria@example.com");
// Não há email.setValue() - tem que criar novo!

// Igualdade por valor
Email e1 = Email.of("joao@example.com");
Email e2 = Email.of("joao@example.com");
e1.equals(e2); // true - mesmo valor
```

### CPF Value Object

```java
// Criação com validação automática
CPF cpf = CPF.of("123.456.789-09");

// Aceita formatado ou não
CPF cpf1 = CPF.of("12345678909");
CPF cpf2 = CPF.of("123.456.789-09");
cpf1.equals(cpf2); // true - mesmo valor

// Métodos úteis
cpf.getValue();      // "12345678909" (sem formatação)
cpf.getFormatted();  // "123.456.789-09" (formatado)
cpf.getMasked();     // "***.***. 789-09" (mascarado)

// Validação automática
CPF.of("111.111.111-11"); // ❌ Exceção - todos iguais
CPF.of("123.456.789-00"); // ❌ Exceção - dígito inválido
CPF.of("invalid");        // ❌ Exceção - formato inválido
```

## ✅ Benefícios

### 1. Type Safety
```java
// ❌ Primitivo - pode confundir
public void enviarEmail(String email, String nome) { }
enviarEmail(nome, email); // Ops! Inverteu!

// ✅ Value Object - impossível confundir
public void enviarEmail(Email email, Nome nome) { }
enviarEmail(nome, email); // Erro de compilação!
```

### 2. Validação Centralizada
```java
// ❌ Validação espalhada
if (email != null && email.contains("@")) { }
if (!email.isEmpty() && email.matches("...")) { }

// ✅ Validação em um único lugar
Email email = Email.of(input); // Sempre válido se existe!
```

### 3. Comportamento Encapsulado
```java
// ❌ Lógica espalhada
String domain = email.substring(email.indexOf('@') + 1);

// ✅ Comportamento no Value Object
String domain = email.getDomain();
```

### 4. Impossível Ter Valor Inválido
```java
// Se um Email existe, ele é válido!
Email email = Email.of("joao@example.com");
// Não precisa validar novamente
```

## 🎨 Quando Usar Value Objects?

### ✅ Use para:

- **Conceitos com regras**: Email, CPF, CNPJ, Telefone
- **Valores monetários**: Dinheiro, Moeda
- **Medidas**: Peso, Altura, Distância
- **Coordenadas**: Latitude, Longitude
- **Intervalos**: DateRange, TimeRange
- **Endereços**: Endereco, CEP

### ❌ Não use para:

- Valores simples sem regras (ex: comentário livre)
- Entidades com identidade (ex: Usuario, Pedido)
- Coleções mutáveis

## 📝 Padrão de Implementação

### Template Básico

```java
public final class MeuValueObject {
    
    private final String value;
    
    // Construtor privado
    private MeuValueObject(String value) {
        this.value = value;
    }
    
    // Factory method com validação
    public static MeuValueObject of(String value) {
        // Validar
        if (!isValid(value)) {
            throw new IllegalArgumentException("Inválido");
        }
        // Normalizar
        String normalized = normalize(value);
        // Criar
        return new MeuValueObject(normalized);
    }
    
    // Getter
    public String getValue() {
        return value;
    }
    
    // Comportamentos úteis
    public String getFormatted() {
        return format(value);
    }
    
    // Equals e HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeuValueObject that = (MeuValueObject) o;
        return Objects.equals(value, that.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}
```

## 🧪 Testando Value Objects

```java
@Test
void deveCriarValueObjectValido() {
    Email email = Email.of("joao@example.com");
    assertNotNull(email);
}

@Test
void deveLancarExcecaoParaValorInvalido() {
    assertThrows(IllegalArgumentException.class, () -> {
        Email.of("invalido");
    });
}

@Test
void deveSerIgualQuandoValorIgual() {
    Email e1 = Email.of("joao@example.com");
    Email e2 = Email.of("joao@example.com");
    assertEquals(e1, e2);
}

@Test
void deveSerImutavel() {
    Email email = Email.of("joao@example.com");
    // Não existe email.setValue()
    // Para mudar, precisa criar novo
}
```

## 🔄 Comparação com Alternativas

### 1. String Primitiva

```java
// ❌ String - sem garantias
String email = "qualquer-coisa"; // Aceita inválido
String cpf = "abc"; // Aceita inválido

// ✅ Value Object - sempre válido
Email email = Email.of("joao@example.com"); // Validado
CPF cpf = CPF.of("123.456.789-09"); // Validado
```

### 2. Bean com Setters

```java
// ❌ Bean mutável
public class EmailBean {
    private String value;
    public void setValue(String v) { this.value = v; }
}
EmailBean email = new EmailBean();
email.setValue("invalido"); // Aceita!

// ✅ Value Object imutável
Email email = Email.of("invalido"); // Lança exceção!
```

### 3. Record (Java 14+)

```java
// ⚠️ Record - imutável mas sem validação
public record EmailRecord(String value) { }
EmailRecord e = new EmailRecord("invalido"); // Aceita!

// ✅ Value Object - imutável E validado
Email email = Email.of("invalido"); // Lança exceção!
```

## 🌟 Exemplos de Outros Value Objects

### Dinheiro
```java
public final class Money {
    private final BigDecimal amount;
    private final Currency currency;
    
    public Money add(Money other) {
        // Retorna NOVO Money
    }
}
```

### Endereço
```java
public final class Endereco {
    private final String rua;
    private final String numero;
    private final CEP cep;
    // Todos final - imutável
}
```

### DateRange
```java
public final class DateRange {
    private final LocalDate start;
    private final LocalDate end;
    
    public boolean contains(LocalDate date) {
        // Lógica encapsulada
    }
}
```

## 📚 Referências

- **Domain-Driven Design** (Eric Evans) - Capítulo sobre Value Objects
- **Implementing Domain-Driven Design** (Vaughn Vernon)
- **Patterns of Enterprise Application Architecture** (Martin Fowler)

## ✅ Checklist Value Object

Um Value Object está correto se:

- [ ] É `final` (classe não pode ser estendida)
- [ ] Todos os campos são `final` (imutável)
- [ ] Não tem setters
- [ ] Valida no construtor/factory
- [ ] `equals()` e `hashCode()` baseados no valor
- [ ] Factory method público, construtor privado
- [ ] Testes cobrem validações
- [ ] Sem dependências de frameworks (puro)

## 🎯 Resumo

> **"Value Objects encapsulam conceitos do domínio com regras de negócio, garantindo que valores inválidos não existam no sistema."**

**Use Value Objects para:**
- Substituir primitivos com regras
- Centralizar validações
- Tornar código mais expressivo
- Garantir type safety
- Encapsular comportamentos

---

**Implementado em:** domain/valueobject/  
**Exemplos:** Email.java, CPF.java  
**Testes:** EmailTest.java, CPFTest.java

