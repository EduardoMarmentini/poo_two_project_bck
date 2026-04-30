package uni.pooII.project_api.dto.mercadorias;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
public class MercadoriaRequestDTO {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 0, message = "Quantidade não pode ser negativa")
    private Integer quantidade;

    @NotNull(message = "Fornecedor é obrigatório")
    private Long fornecedorId;

    private String descricao;
    private LocalDate dataValidade;
}