package uni.pooII.project_api.dto.mercadorias;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MovimentacaoRequestDTO {
    @NotBlank(message = "Tipo é obrigatório (ENTRADA/SAIDA)")
    private String tipo; // ENTRADA ou SAIDA

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser maior que zero")
    private Integer quantidade;
}
