package uni.pooII.project_api.mapper;

import uni.pooII.project_api.dto.fornecedores.*;
import uni.pooII.project_api.model.*;

import java.time.LocalDateTime;

public class FornecedorMapper {

    public static Fornecedor toEntity(FornecedorRequestDTO dto) {
        return Fornecedor.builder()
                .nome(dto.getNome())
                .contato(dto.getContato())
                .endereco(
                        Endereco.builder()
                                .rua(dto.getRua())
                                .numero(dto.getNumero())
                                .complemento(dto.getComplemento())
                                .bairro(dto.getBairro())
                                .cidade(dto.getCidade())
                                .estado(dto.getEstado())
                                .cep(dto.getCep())
                                .build()
                )
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static FornecedorResponseDTO toResponse(Fornecedor entity) {
        return FornecedorResponseDTO.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .contato(entity.getContato())
                .rua(entity.getEndereco().getRua())
                .numero(entity.getEndereco().getNumero())
                .complemento(entity.getEndereco().getComplemento())
                .bairro(entity.getEndereco().getBairro())
                .cidade(entity.getEndereco().getCidade())
                .estado(entity.getEndereco().getEstado())
                .cep(entity.getEndereco().getCep())
                .build();
    }
}