package uni.pooII.project_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uni.pooII.project_api.dto.mercadorias.MercadoriaRequestDTO;
import uni.pooII.project_api.dto.mercadorias.MercadoriaResponseDTO;
import uni.pooII.project_api.dto.mercadorias.MercadoriaPatchDTO;
import uni.pooII.project_api.dto.mercadorias.MovimentacaoRequestDTO;
import uni.pooII.project_api.service.MercadoriaService;

import java.util.List;

@RestController
@RequestMapping("/mercadorias")
@RequiredArgsConstructor
public class MercadoriaController {

    private final MercadoriaService service;

    // CREATE - ADMIN e MANAGER
    @PostMapping
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','SYSTEM_MANAGER')")
    public ResponseEntity<MercadoriaResponseDTO> criar(
            @RequestBody @Valid MercadoriaRequestDTO dto) {
        return ResponseEntity.status(201).body(service.criar(dto));
    }

    // LISTAR - todos autenticados
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','SYSTEM_MANAGER','SYSTEM_USER')")
    public ResponseEntity<List<MercadoriaResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    // BUSCAR POR ID - todos
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','SYSTEM_MANAGER','SYSTEM_USER')")
    public ResponseEntity<MercadoriaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // UPDATE - ADMIN e MANAGER
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','SYSTEM_MANAGER')")
    public ResponseEntity<MercadoriaResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid MercadoriaRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    // UPDATE PARCIAL - ADMIN e MANAGER (SYSTEM_USER usa movimentação)
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','SYSTEM_MANAGER')")
    public ResponseEntity<MercadoriaResponseDTO> atualizarParcial(
            @PathVariable Long id,
            @RequestBody MercadoriaPatchDTO dto) {
        return ResponseEntity.ok(service.atualizarParcial(id, dto));
    }

    // MOVIMENTAÇÃO DE ESTOQUE - todos (entrada/saída)
    @PostMapping("/{id}/movimentacao")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','SYSTEM_MANAGER','SYSTEM_USER')")
    public ResponseEntity<MercadoriaResponseDTO> movimentar(
            @PathVariable Long id,
            @RequestBody @Valid MovimentacaoRequestDTO dto) {
        return ResponseEntity.ok(service.movimentar(id, dto));
    }

    // DELETE - ADMIN e MANAGER
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','SYSTEM_MANAGER')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // LISTAR POR FORNECEDOR - todos
    @GetMapping("/fornecedor/{fornecedorId}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','SYSTEM_MANAGER','SYSTEM_USER')")
    public ResponseEntity<List<MercadoriaResponseDTO>> listarPorFornecedor(@PathVariable Long fornecedorId) {
        return ResponseEntity.ok(service.listarPorFornecedor(fornecedorId));
    }
}