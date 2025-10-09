package com.example.domain;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Testes Arquiteturais para garantir que o Domain permanece puro
 * 
 * ⚠️ IMPORTANTE: Estes testes garantem que o Domain não seja poluído com frameworks
 */
class ArchitectureTest {
    
    private static final List<String> FORBIDDEN_IMPORTS = List.of(
        "org.springframework",
        "jakarta.persistence",
        "org.hibernate",
        "javax.persistence"
    );
    
    @Test
    void domainNaoDeveImportarSpring() throws IOException {
        List<String> violations = new ArrayList<>();
        
        Path domainPath = Paths.get("src/main/java");
        
        if (!Files.exists(domainPath)) {
            return; // Não está rodando do diretório correto, pular teste
        }
        
        try (Stream<Path> paths = Files.walk(domainPath)) {
            paths.filter(Files::isRegularFile)
                 .filter(p -> p.toString().endsWith(".java"))
                 .forEach(path -> {
                     try {
                         List<String> lines = Files.readAllLines(path);
                         for (int i = 0; i < lines.size(); i++) {
                             String line = lines.get(i).trim();
                             if (line.startsWith("import ")) {
                                 for (String forbidden : FORBIDDEN_IMPORTS) {
                                     if (line.contains(forbidden)) {
                                         violations.add(String.format(
                                             "%s:%d - Import proibido: %s",
                                             path.getFileName(),
                                             i + 1,
                                             line
                                         ));
                                     }
                                 }
                             }
                         }
                     } catch (IOException e) {
                         // Ignorar erro de leitura
                     }
                 });
        }
        
        if (!violations.isEmpty()) {
            StringBuilder message = new StringBuilder();
            message.append("\n\n❌ VIOLAÇÃO ARQUITETURAL DETECTADA!\n\n");
            message.append("O módulo Domain não pode importar frameworks!\n");
            message.append("Domain deve ser PURO e independente.\n\n");
            message.append("Violações encontradas:\n");
            violations.forEach(v -> message.append("  - ").append(v).append("\n"));
            message.append("\n");
            message.append("Imports proibidos:\n");
            FORBIDDEN_IMPORTS.forEach(f -> message.append("  - ").append(f).append(".*\n"));
            message.append("\n");
            
            fail(message.toString());
        }
    }
    
    @Test
    void domainNaoDeveUsarAnotacoesDeFrameworks() throws IOException {
        List<String> violations = new ArrayList<>();
        
        List<String> forbiddenAnnotations = List.of(
            "@Entity",
            "@Table",
            "@Column",
            "@Id",
            "@Service",
            "@Component",
            "@Repository",
            "@Controller",
            "@RestController",
            "@Autowired",
            "@Inject"
        );
        
        Path domainPath = Paths.get("src/main/java");
        
        if (!Files.exists(domainPath)) {
            return;
        }
        
        try (Stream<Path> paths = Files.walk(domainPath)) {
            paths.filter(Files::isRegularFile)
                 .filter(p -> p.toString().endsWith(".java"))
                 .forEach(path -> {
                     try {
                         List<String> lines = Files.readAllLines(path);
                         for (int i = 0; i < lines.size(); i++) {
                             String line = lines.get(i).trim();
                             for (String annotation : forbiddenAnnotations) {
                                 if (line.startsWith(annotation)) {
                                     violations.add(String.format(
                                         "%s:%d - Anotação proibida: %s",
                                         path.getFileName(),
                                         i + 1,
                                         annotation
                                     ));
                                 }
                             }
                         }
                     } catch (IOException e) {
                         // Ignorar
                     }
                 });
        }
        
        if (!violations.isEmpty()) {
            StringBuilder message = new StringBuilder();
            message.append("\n\n❌ VIOLAÇÃO ARQUITETURAL DETECTADA!\n\n");
            message.append("O módulo Domain não pode usar anotações de frameworks!\n\n");
            message.append("Violações encontradas:\n");
            violations.forEach(v -> message.append("  - ").append(v).append("\n"));
            message.append("\n");
            
            fail(message.toString());
        }
    }
}

