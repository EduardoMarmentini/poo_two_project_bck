package uni.pooII.project_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uni.pooII.project_api.dto.mercadorias.MercadoriaRequestDTO;
import uni.pooII.project_api.dto.mercadorias.MercadoriaResponseDTO;
import uni.pooII.project_api.service.MercadoriaService;

import java.util.List;

@RestController
@RequestMapping("/mercadorias")
@RequiredArgsConstructor
public class MercadoriaController {

    private final MercadoriaService service;

    // CREATE
    @PostMapping
    public ResponseEntity<MercadoriaResponseDTO> criar(
            @RequestBody @Valid MercadoriaRequestDTO dto) {

        return ResponseEntity.status(201).body(service.criar(dto));
    }

    // LISTAR
    @GetMapping
    public ResponseEntity<List<MercadoriaResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<MercadoriaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<MercadoriaResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid MercadoriaRequestDTO dto) {

        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // LISTAR POR FORNECEDOR
    @GetMapping("/fornecedor/{fornecedorId}")
    public ResponseEntity<List<MercadoriaResponseDTO>> listarPorFornecedor(@PathVariable Long fornecedorId) {
        return ResponseEntity.ok(service.listarPorFornecedor(fornecedorId));
    }
}