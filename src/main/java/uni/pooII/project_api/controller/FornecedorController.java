package uni.pooII.project_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uni.pooII.project_api.dto.fornecedores.*;
import uni.pooII.project_api.service.FornecedorService;

import java.util.List;

@RestController
@RequestMapping("/fornecedores")
@RequiredArgsConstructor
public class FornecedorController {

    private final FornecedorService service;

    // CREATE - ADMIN e MANAGER
    @PostMapping
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','SYSTEM_MANAGER')")
    public ResponseEntity<FornecedorResponseDTO> criar(
            @RequestBody @Valid FornecedorRequestDTO dto) {
        return ResponseEntity.status(201).body(service.criar(dto));
    }

    // LISTAR - todos
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','SYSTEM_MANAGER','SYSTEM_USER')")
    public ResponseEntity<List<FornecedorResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    // BUSCAR POR ID - todos
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','SYSTEM_MANAGER','SYSTEM_USER')")
    public ResponseEntity<FornecedorResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // UPDATE - ADMIN e MANAGER
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','SYSTEM_MANAGER')")
    public ResponseEntity<FornecedorResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid FornecedorRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    // DELETE - ADMIN e MANAGER
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','SYSTEM_MANAGER')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}