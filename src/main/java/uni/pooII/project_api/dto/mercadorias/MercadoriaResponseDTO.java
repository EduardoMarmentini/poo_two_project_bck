package uni.pooII.project_api.dto.mercadorias;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MercadoriaResponseDTO {

    private Long id;
    private String nome;
    private String descricao;
    private LocalDate dataValidade;
    private LocalDateTime dataCadastro;
    private Integer quantidade;

    private Long fornecedorId;
    private String nomeFornecedor;
}