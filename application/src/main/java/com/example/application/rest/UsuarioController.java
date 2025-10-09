package com.example.application.rest;

import com.example.application.rest.dto.UsuarioRequest;
import com.example.application.rest.dto.UsuarioResponse;
import com.example.application.service.command.UsuarioCommandService;
import com.example.application.service.query.UsuarioQueryService;
import com.example.application.service.query.dto.UsuarioListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Adaptador de Entrada - Controller REST
 * 
 * Agora usa CQRS (Command Query Responsibility Segregation):
 * - Commands (write) → UsuarioCommandService → Domain
 * - Queries (read) → UsuarioQueryService → Infrastructure (bypass)
 * 
 * Responsabilidade do Controller: 
 * - Receber requisições HTTP
 * - Delegar para o service apropriado
 * - Retornar respostas HTTP
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    
    private final UsuarioCommandService commandService;
    private final UsuarioQueryService queryService;
    
    /**
     * CREATE - Command (passa por Domain)
     * POST tem lógica de negócio, validações, regras
     */
    @PostMapping
    public ResponseEntity<UsuarioResponse> criarUsuario(@RequestBody UsuarioRequest request) {
        UsuarioResponse response = commandService.criarUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * READ - Query (bypass Domain)
     * GET simples sem lógica, apenas busca dados
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarUsuario(@PathVariable Long id) {
        return queryService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * READ - Query (bypass Domain)
     * Listagem simples sem lógica
     */
    @GetMapping
    public ResponseEntity<List<UsuarioListResponse>> listarUsuarios() {
        List<UsuarioListResponse> usuarios = queryService.listarTodosUsuarios();
        return ResponseEntity.ok(usuarios);
    }
    
    /**
     * READ - Query por email (bypass Domain)
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioResponse> buscarPorEmail(@PathVariable String email) {
        return queryService.buscarPorEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * READ - Query de estatística (bypass Domain)
     */
    @GetMapping("/count")
    public ResponseEntity<Long> contarUsuarios() {
        Long count = queryService.contarUsuarios();
        return ResponseEntity.ok(count);
    }
    
    /**
     * DELETE - Command (passa por Domain)
     * DELETE tem lógica: verificar existência, possíveis validações
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerUsuario(@PathVariable Long id) {
        commandService.removerUsuario(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * UPDATE - Command (passa por Domain)
     * PUT/PATCH tem lógica de negócio
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizarUsuario(
            @PathVariable Long id,
            @RequestBody UsuarioRequest request) {
        UsuarioResponse response = commandService.atualizarUsuario(id, request);
        return ResponseEntity.ok(response);
    }
}

