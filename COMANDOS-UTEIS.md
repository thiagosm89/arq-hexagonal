# Comandos Úteis

## 🔨 Gradle

### Compilar todo o projeto
```bash
./gradlew build
```

### Compilar apenas um módulo
```bash
./gradlew :domain:build
./gradlew :infrastructure:build
./gradlew :application:build
```

### Executar a aplicação
```bash
./gradlew :application:bootRun
```

### Executar testes
```bash
# Todos os testes
./gradlew test

# Testes de um módulo específico
./gradlew :domain:test
./gradlew :application:test

# Com relatório detalhado
./gradlew test --info
```

### Limpar build
```bash
./gradlew clean
```

### Ver dependências de um módulo
```bash
./gradlew :domain:dependencies
./gradlew :infrastructure:dependencies
./gradlew :application:dependencies
```

### Gerar JAR executável
```bash
./gradlew :application:bootJar
java -jar application/build/libs/application-0.0.1-SNAPSHOT.jar
```

## 🔍 Verificações de Arquitetura

### Verificar que Domain não tem dependências do Spring
```bash
# Windows PowerShell
Select-String -Path "domain\src\main\java\**\*.java" -Pattern "org.springframework" -Recurse

# Linux/Mac
grep -r "org.springframework" domain/src/main/java/
```
**Resultado esperado:** Nenhuma ocorrência

### Verificar estrutura dos módulos
```bash
# Windows
tree /F domain\src\main\java
tree /F infrastructure\src\main\java
tree /F application\src\main\java

# Linux/Mac
tree domain/src/main/java
tree infrastructure/src/main/java
tree application/src/main/java
```

## 🧪 Testes

### Executar teste específico
```bash
./gradlew test --tests UsuarioUseCaseImplTest
./gradlew test --tests ApplicationIntegrationTest
```

### Executar com cobertura
```bash
./gradlew test jacocoTestReport
```

### Ver resultado dos testes
```bash
# Abre o relatório HTML no navegador
start build/reports/tests/test/index.html  # Windows
open build/reports/tests/test/index.html   # Mac
xdg-open build/reports/tests/test/index.html  # Linux
```

## 🌐 Executar e Testar API

### Iniciar aplicação
```bash
./gradlew :application:bootRun
```

### Testar endpoints (via curl)

**Criar usuário:**
```bash
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d "{\"nome\":\"João Silva\",\"email\":\"joao@example.com\"}"
```

**Listar todos:**
```bash
curl http://localhost:8080/api/usuarios
```

**Buscar por ID:**
```bash
curl http://localhost:8080/api/usuarios/1
```

**Remover:**
```bash
curl -X DELETE http://localhost:8080/api/usuarios/1
```

### Acessar console H2
1. Executar aplicação
2. Abrir navegador em: http://localhost:8080/h2-console
3. Credenciais:
   - JDBC URL: `jdbc:h2:mem:testdb`
   - Username: `sa`
   - Password: (vazio)

## 📦 Gerenciamento de Dependências

### Verificar versões disponíveis
```bash
./gradlew dependencyUpdates
```

### Listar todas as tarefas disponíveis
```bash
./gradlew tasks
```

### Refresh de dependências
```bash
./gradlew build --refresh-dependencies
```

## 🐛 Debug

### Executar em modo debug
```bash
./gradlew :application:bootRun --debug-jvm
```
Conecte seu IDE na porta 5005

### Ver logs detalhados
```bash
./gradlew :application:bootRun --info
./gradlew :application:bootRun --debug
```

## 🔧 Gradle Wrapper

### Atualizar Gradle Wrapper
```bash
./gradlew wrapper --gradle-version=8.5
```

### Verificar versão do Gradle
```bash
./gradlew --version
```

## 📊 Análise de Código

### Verificar estilo de código (se Checkstyle configurado)
```bash
./gradlew checkstyleMain
```

### Análise estática (se SpotBugs configurado)
```bash
./gradlew spotbugsMain
```

## 🚀 Deploy

### Criar JAR otimizado para produção
```bash
./gradlew :application:bootJar -Pprofile=prod
```

### Executar com profile de produção
```bash
java -jar -Dspring.profiles.active=prod application/build/libs/application-0.0.1-SNAPSHOT.jar
```

## 📝 Notas

- Use `./gradlew` no Linux/Mac
- Use `gradlew.bat` ou `.\gradlew` no Windows PowerShell
- Para comandos paralelos: `./gradlew task1 task2 task3`
- Para forçar execução: `./gradlew task --rerun-tasks`

