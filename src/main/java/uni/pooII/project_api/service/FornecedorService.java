package uni.pooII.project_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uni.pooII.project_api.dto.fornecedores.*;
import uni.pooII.project_api.exception.NotFoundException;
import uni.pooII.project_api.mapper.FornecedorMapper;
import uni.pooII.project_api.model.Fornecedor;
import uni.pooII.project_api.repository.FornecedorRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FornecedorService {

    private final FornecedorRepository repository;

    // CREATE
    public FornecedorResponseDTO criar(FornecedorRequestDTO dto) {
        Fornecedor fornecedor = FornecedorMapper.toEntity(dto);
        return FornecedorMapper.toResponse(repository.save(fornecedor));
    }

    // LISTAR
    public List<FornecedorResponseDTO> listar() {
        return repository.findAll()
                .stream()
                .map(FornecedorMapper::toResponse)
                .toList();
    }

    // BUSCAR POR ID
    public FornecedorResponseDTO buscarPorId(Long id) {
        Fornecedor fornecedor = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Fornecedor não encontrado"));

        return FornecedorMapper.toResponse(fornecedor);
    }

    // UPDATE
    public FornecedorResponseDTO atualizar(Long id, FornecedorRequestDTO dto) {
        Fornecedor fornecedor = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Fornecedor não encontrado"));

        fornecedor.setNome(dto.getNome());
        fornecedor.setContato(dto.getContato());

        fornecedor.getEndereco().setRua(dto.getRua());
        fornecedor.getEndereco().setNumero(dto.getNumero());
        fornecedor.getEndereco().setComplemento(dto.getComplemento());
        fornecedor.getEndereco().setBairro(dto.getBairro());
        fornecedor.getEndereco().setCidade(dto.getCidade());
        fornecedor.getEndereco().setEstado(dto.getEstado());
        fornecedor.getEndereco().setCep(dto.getCep());

        return FornecedorMapper.toResponse(repository.save(fornecedor));
    }

    // DELETE
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Fornecedor não encontrado");
        }
        repository.deleteById(id);
    }
}