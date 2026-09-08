package uni.pooII.project_api.dto.auth;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegisterRequestDTO {
    @NotBlank(message = "Username é obrigatório")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "Email é obrigatório")
    @Email
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 100)
    private String password;

    @NotEmpty(message = "Ao menos uma role é obrigatória")
    private java.util.Set<String> roles;
}
