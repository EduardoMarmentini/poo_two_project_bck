package uni.pooII.project_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uni.pooII.project_api.dto.mercadorias.MercadoriaRequestDTO;
import uni.pooII.project_api.dto.mercadorias.MercadoriaResponseDTO;
import uni.pooII.project_api.dto.mercadorias.MercadoriaPatchDTO;
import uni.pooII.project_api.dto.mercadorias.MovimentacaoRequestDTO;
import uni.pooII.project_api.exception.BadRequestException;
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

    // LISTAR
    public List<MercadoriaResponseDTO> listar() {
        return repository.findAll()
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

    // UPDATE PARCIAL (PATCH)
    public MercadoriaResponseDTO atualizarParcial(Long id, MercadoriaPatchDTO dto) {
        Mercadoria mercadoria = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mercadoria não encontrada"));

        // atualiza apenas os campos fornecidos
        dto.getNome().ifPresent(mercadoria::setNome);
        dto.getDescricao().ifPresent(mercadoria::setDescricao);
        dto.getDataValidade().ifPresent(mercadoria::setDataValidade);
        dto.getQuantidade().ifPresent(mercadoria::setQuantidade);

        // se o fornecedor foi fornecido atualiza
        if (dto.getFornecedorId().isPresent()) {
            Fornecedor fornecedor = fornecedorRepository.findById(dto.getFornecedorId().get())
                    .orElseThrow(() -> new NotFoundException("Fornecedor não encontrado"));
            mercadoria.setFornecedor(fornecedor);
        }

        return MercadoriaMapper.toResponse(repository.save(mercadoria));
    }

    // LISTAR POR FORNECEDOR 
    public List<MercadoriaResponseDTO> listarPorFornecedor(Long fornecedorId) {
        if (!fornecedorRepository.existsById(fornecedorId)) {
            throw new NotFoundException("Fornecedor não encontrado");
        }

        return repository.findByFornecedorId(fornecedorId)
                .stream()
                .map(MercadoriaMapper::toResponse)
                .toList();
    }

    // MOVIMENTAÇÃO DE ESTOQUE (ENTRADA/SAIDA) - permitido para todos perfis
    public MercadoriaResponseDTO movimentar(Long id, MovimentacaoRequestDTO dto) {
        Mercadoria mercadoria = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mercadoria não encontrada"));

        String tipo = dto.getTipo().toUpperCase();
        if (!tipo.equals("ENTRADA") && !tipo.equals("SAIDA")) {
            throw new BadRequestException("Tipo deve ser ENTRADA ou SAIDA");
        }

        int novaQtd;
        if (tipo.equals("ENTRADA")) {
            novaQtd = mercadoria.getQuantidade() + dto.getQuantidade();
        } else {
            novaQtd = mercadoria.getQuantidade() - dto.getQuantidade();
            if (novaQtd < 0) novaQtd = 0;
        }

        mercadoria.setQuantidade(novaQtd);
        return MercadoriaMapper.toResponse(repository.save(mercadoria));
    }
}
