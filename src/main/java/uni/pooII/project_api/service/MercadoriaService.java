package uni.pooII.project_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uni.pooII.project_api.dto.mercadorias.MercadoriaRequestDTO;
import uni.pooII.project_api.dto.mercadorias.MercadoriaResponseDTO;
import uni.pooII.project_api.exception.NotFoundException;
import uni.pooII.project_api.mapper.MercadoriaMapper;
import uni.pooII.project_api.model.Fornecedor;
import uni.pooII.project_api.model.Mercadoria;
import uni.pooII.project_api.repository.FornecedorRepository;
import uni.pooII.project_api.repository.MercadoriaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MercadoriaService {

    private final MercadoriaRepository repository;
    private final FornecedorRepository fornecedorRepository;

    // CREATE
    public MercadoriaResponseDTO criar(MercadoriaRequestDTO dto) {
        Fornecedor fornecedor = fornecedorRepository.findById(dto.getFornecedorId())
                .orElseThrow(() -> new NotFoundException("Fornecedor não encontrado"));

        Mercadoria mercadoria = MercadoriaMapper.toEntity(dto, fornecedor);
        return MercadoriaMapper.toResponse(repository.save(mercadoria));
    }

    // LISTAR (apenas mercadorias com estoque > 0)
    public List<MercadoriaResponseDTO> listar() {
        return repository.findByQuantidadeGreaterThan(0)
                .stream()
                .map(MercadoriaMapper::toResponse)
                .toList();
    }

    // BUSCAR POR ID
    public MercadoriaResponseDTO buscarPorId(Long id) {
        Mercadoria mercadoria = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mercadoria não encontrada"));

        return MercadoriaMapper.toResponse(mercadoria);
    }

    // UPDATE
    public MercadoriaResponseDTO atualizar(Long id, MercadoriaRequestDTO dto) {
        Mercadoria mercadoria = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mercadoria não encontrada"));

        Fornecedor fornecedor = fornecedorRepository.findById(dto.getFornecedorId())
                .orElseThrow(() -> new NotFoundException("Fornecedor não encontrado"));

        mercadoria.setNome(dto.getNome());
        mercadoria.setDescricao(dto.getDescricao());
        mercadoria.setDataValidade(dto.getDataValidade());
        mercadoria.setQuantidade(dto.getQuantidade());
        mercadoria.setFornecedor(fornecedor);

        return MercadoriaMapper.toResponse(repository.save(mercadoria));
    }

    // DELETE
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Mercadoria não encontrada");
        }
        repository.deleteById(id);
    }

    // LISTAR POR FORNECEDOR (apenas mercadorias com estoque > 0)
    public List<MercadoriaResponseDTO> listarPorFornecedor(Long fornecedorId) {
        if (!fornecedorRepository.existsById(fornecedorId)) {
            throw new NotFoundException("Fornecedor não encontrado");
        }

        return repository.findByFornecedorId(fornecedorId)
                .stream()
                .filter(m -> m.getQuantidade() > 0)
                .map(MercadoriaMapper::toResponse)
                .toList();
    }
}
