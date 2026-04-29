package uni.pooII.project_api.dto.mercadorias;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MercadoriaRequestDTO {

    private String nome;
    private String descricao;
    private LocalDate dataValidade;
    private Integer quantidade;

    private Long fornecedorId;
}