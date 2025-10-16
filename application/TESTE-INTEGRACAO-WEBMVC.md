# 🌐 Teste de Integração WebMvc com Mock Automático

Este documento explica como implementamos **testes de integração WebMvc** que testam o fluxo completo da aplicação via HTTP, mantendo o **mock automático de repositórios**.

## 🎯 Objetivo

Testar o **fluxo completo** da aplicação web:
```
HTTP Request → Controller → CommandService → InboundPort → Repository (Mockado)
```

## 🔧 Arquitetura da Solução

### 1. **Configuração do Teste**
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Import(AutoMockRepositoryConfiguration.class)
class UsuarioIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate; // Para chamadas HTTP
    
    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository; // Automaticamente mockado
}
```

### 2. **Testes com @DisplayName**
```java
@Test
@DisplayName("Deve criar usuário via POST /api/usuarios e retornar 201 Created")
void deveCriarUsuarioViaHttp() {
    // Mock explícito quando necessário
    when(usuarioJpaRepository.save(any())).thenReturn(entity);
    
    // Teste via HTTP
    ResponseEntity<UsuarioResponse> response = restTemplate.postForEntity(
        "/api/usuarios", request, UsuarioResponse.class
    );
    
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
}
```

---

## ⚡ Como Funciona

### 1. **TestRestTemplate**
- Faz chamadas HTTP reais para o servidor
- Testa serialização/deserialização JSON
- Valida status codes e headers
- Simula cliente real da API

### 2. **Mock Automático Mantido**
- `@Import(AutoMockRepositoryConfiguration.class)` continua funcionando
- Repositórios são automaticamente mockados
- Testes podem fazer mock explícito quando necessário

### 3. **Fluxo Completo Testado**
```
HTTP POST /api/usuarios
   ↓
UsuarioController.criarUsuario()
   ↓
UsuarioCommandService.criarUsuario()
   ↓
UsuarioInboundPort.criarUsuario()
   ↓
UsuarioRepositoryAdapter.salvar()
   ↓
UsuarioJpaRepository.save() (MOCKADO)
```

---

## 📋 Testes Implementados

### ✅ **Teste de Criação (POST)**
```java
@Test
@DisplayName("Deve criar usuário via POST /api/usuarios e retornar 201 Created")
void deveCriarUsuarioViaHttp() {
    // Mock do repositório
    when(usuarioJpaRepository.save(any())).thenReturn(entity);
    
    // Request HTTP
    UsuarioRequest request = new UsuarioRequest();
    request.setNome("Teste HTTP");
    request.setEmail("teste.http@example.com");
    request.setCpf("80333508068");
    
    // Chamada HTTP POST
    ResponseEntity<UsuarioResponse> response = restTemplate.postForEntity(
        "/api/usuarios", request, UsuarioResponse.class
    );
    
    // Verificações
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("Teste HTTP", response.getBody().getNome());
}
```

### ✅ **Teste de Busca por ID (GET)**
```java
@Test
@DisplayName("Deve buscar usuário por ID via GET /api/usuarios/{id} e retornar 200 OK")
void deveBuscarUsuarioPorIdViaHttp() {
    // Mock do repositório
    when(usuarioJpaRepository.findById(1L)).thenReturn(Optional.of(entity));
    
    // Chamada HTTP GET
    ResponseEntity<UsuarioResponse> response = restTemplate.getForEntity(
        "/api/usuarios/1", UsuarioResponse.class
    );
    
    // Verificações
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Usuario Busca", response.getBody().getNome());
}
```

### ✅ **Teste de Listagem (GET)**
```java
@Test
@DisplayName("Deve listar todos usuários via GET /api/usuarios e retornar 200 OK")
void deveListarTodosUsuariosViaHttp() {
    // Mock do repositório
    when(usuarioJpaRepository.findAll()).thenReturn(List.of(entity1, entity2));
    
    // Chamada HTTP GET
    ResponseEntity<UsuarioResponse[]> response = restTemplate.getForEntity(
        "/api/usuarios", UsuarioResponse[].class
    );
    
    // Verificações
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(2, response.getBody().length);
}
```

### ✅ **Teste de Remoção (DELETE)**
```java
@Test
@DisplayName("Deve remover usuário via DELETE /api/usuarios/{id} e retornar 204 No Content")
void deveRemoverUsuarioViaHttp() {
    // Mock do repositório
    when(usuarioJpaRepository.findById(1L)).thenReturn(Optional.of(entity));
    
    // Chamada HTTP DELETE
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/usuarios/1", HttpMethod.DELETE, null, Void.class
    );
    
    // Verificações
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
}
```

### ✅ **Teste de Erro 404**
```java
@Test
@DisplayName("Deve retornar 404 Not Found quando buscar usuário inexistente")
void deveRetornar404ParaUsuarioInexistente() {
    // Mock: usuário não encontrado
    when(usuarioJpaRepository.findById(999L)).thenReturn(Optional.empty());
    
    // Chamada HTTP GET para usuário inexistente
    ResponseEntity<UsuarioResponse> response = restTemplate.getForEntity(
        "/api/usuarios/999", UsuarioResponse.class
    );
    
    // Verificações
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
}
```

### ✅ **Teste de Erro 400**
```java
@Test
@DisplayName("Deve retornar 400 Bad Request quando criar usuário com dados inválidos")
void deveRetornar400ParaDadosInvalidos() {
    // Request com dados inválidos
    UsuarioRequest request = new UsuarioRequest();
    request.setNome(""); // Nome vazio
    request.setEmail("email-invalido"); // Email inválido
    request.setCpf("123"); // CPF inválido
    
    // Chamada HTTP POST com dados inválidos
    ResponseEntity<String> response = restTemplate.postForEntity(
        "/api/usuarios", request, String.class
    );
    
    // Verificações
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
}
```

---

## 🎯 Vantagens da Abordagem

### ✅ **Teste Realístico**
- Simula chamadas HTTP reais
- Testa serialização/deserialização JSON
- Valida status codes e headers HTTP
- Testa integração com Spring MVC

### ✅ **Fluxo Completo**
- Controller → CommandService → InboundPort → Repository
- Validação de todas as camadas
- Testa conversão de DTOs
- Verifica mapeamento de endpoints

### ✅ **Mock Automático Mantido**
- Repositórios são automaticamente mockados
- Novos repositórios funcionam automaticamente
- Testes podem fazer mock explícito quando necessário
- Zero configuração manual

### ✅ **@DisplayName Descritivo**
- Testes com nomes claros e descritivos
- Fácil entender o que cada teste valida
- Melhor documentação dos cenários
- Facilita manutenção e debugging

---

## 📊 Exemplo de Execução

### Console Output:
```
🔍 === CONFIGURAÇÃO AUTOMÁTICA DE MOCK DE REPOSITÓRIOS ===
🎭 Mockando repositório: UsuarioJpaRepository
✅ Total de repositórios mockados automaticamente: 1
===============================================

✅ Contexto carregado com WebMvc e repositórios automaticamente mockados

🎭 === DEMONSTRAÇÃO DO MOCK AUTOMÁTICO ===
✅ UsuarioJpaRepository está automaticamente mockado
✅ Teste pode fazer mock explícito quando necessário
✅ Comportamento padrão é MockBean (não executa métodos reais)
✅ Testa fluxo completo via WebMvc: Controller → CommandService → InboundPort → Repository
🎉 Mock automático funcionando perfeitamente!
```

### Resultado dos Testes:
```
✅ Deve carregar o contexto Spring com WebMvc e repositórios mockados automaticamente
✅ Deve criar usuário via POST /api/usuarios e retornar 201 Created
✅ Deve buscar usuário por ID via GET /api/usuarios/{id} e retornar 200 OK
✅ Deve listar todos usuários via GET /api/usuarios e retornar 200 OK
✅ Deve remover usuário via DELETE /api/usuarios/{id} e retornar 204 No Content
✅ Deve retornar 404 Not Found quando buscar usuário inexistente via GET /api/usuarios/{id}
✅ Deve retornar 400 Bad Request quando criar usuário com dados inválidos
✅ Deve demonstrar que o mock automático está funcionando corretamente
```

---

## 🛠️ Configuração Necessária

### 1. **Dependências (build.gradle)**
```gradle
testImplementation 'org.springframework.boot:spring-boot-starter-test'
```

### 2. **Anotações do Teste**
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Import(AutoMockRepositoryConfiguration.class)
```

### 3. **Injeção de Dependências**
```java
@Autowired
private TestRestTemplate restTemplate; // Para chamadas HTTP

@Autowired
private UsuarioJpaRepository usuarioJpaRepository; // Automaticamente mockado
```

---

## ⚠️ Considerações

### ✅ **Quando Usar:**
- Testes de integração que precisam validar fluxo completo
- Validação de APIs REST
- Testes de serialização/deserialização
- Validação de status HTTP e headers

### ❌ **Quando NÃO Usar:**
- Testes unitários (use mocks tradicionais)
- Testes de performance (são mais lentos)
- Testes que não precisam de contexto Spring

### 🔧 **Limitações:**
- Mais lentos que testes unitários
- Requer contexto Spring completo
- Dependem de configuração de teste

---

## 🎉 Resultado Final

### ✅ **Benefícios Alcançados:**
- Teste de integração realístico via WebMvc
- Mock automático de repositórios mantido
- Fluxo completo testado (HTTP → Controller → Domain)
- Testes com @DisplayName descritivos
- Validação de status HTTP e serialização
- Zero configuração manual para novos repositórios

### ✅ **Arquitetura Preservada:**
- Domain permanece puro
- Hexagonal architecture respeitada
- Separação de responsabilidades mantida
- Mock automático funciona para novos repositórios

---

**🌐 Com esta abordagem, conseguimos testes de integração WebMvc completos que validam o fluxo real da aplicação, mantendo o mock automático de repositórios!**
