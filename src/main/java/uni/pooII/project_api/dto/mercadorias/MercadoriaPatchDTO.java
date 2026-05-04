package uni.pooII.project_api.dto.mercadorias;

import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDate;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MercadoriaPatchDTO {

    private Optional<String> nome = Optional.empty();
    private Optional<String> descricao = Optional.empty();
    private Optional<LocalDate> dataValidade = Optional.empty();

    @Min(value = 0, message = "Quantidade não pode ser negativa")
    private Optional<Integer> quantidade = Optional.empty();

    private Optional<Long> fornecedorId = Optional.empty();
}
