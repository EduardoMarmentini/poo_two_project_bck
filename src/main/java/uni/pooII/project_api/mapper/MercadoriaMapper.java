package uni.pooII.project_api.mapper;

import uni.pooII.project_api.dto.mercadorias.*;
import uni.pooII.project_api.model.*;

import java.time.LocalDateTime;

public class MercadoriaMapper {

    public static Mercadoria toEntity(MercadoriaRequestDTO dto, Fornecedor fornecedor) {
        return Mercadoria.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .dataValidade(dto.getDataValidade())
                .quantidade(dto.getQuantidade())
                .fornecedor(fornecedor)
                .dataCadastro(LocalDateTime.now())
                .build();
    }

    public static MercadoriaResponseDTO toResponse(Mercadoria entity) {
        return MercadoriaResponseDTO.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .dataValidade(entity.getDataValidade())
                .dataCadastro(entity.getDataCadastro())
                .quantidade(entity.getQuantidade())
                .fornecedorId(entity.getFornecedor().getId())
                .nomeFornecedor(entity.getFornecedor().getNome())
                .build();
    }
}