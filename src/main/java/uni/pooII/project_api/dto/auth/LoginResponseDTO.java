package uni.pooII.project_api.dto.auth;

import lombok.*;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginResponseDTO {
    private String accessToken;
    private String idToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private String username;
    private String email;
    private Set<String> roles;
}
