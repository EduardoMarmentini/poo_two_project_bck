package uni.pooII.project_api.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginRequestDTO {
    @NotBlank(message = "Username é obrigatório")
    private String username;
    @NotBlank(message = "Senha é obrigatória")
    private String password;
}
