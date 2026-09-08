package uni.pooII.project_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import uni.pooII.project_api.dto.auth.*;
import uni.pooII.project_api.service.AuthService;
import uni.pooII.project_api.security.JwtTokenProvider;
import uni.pooII.project_api.repository.UserRepository;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<UserResponseDTO> register(@RequestBody @Valid RegisterRequestDTO dto) {
        return ResponseEntity.status(201).body(authService.register(dto));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(authService.me(userDetails.getUsername()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || !tokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(401).build();
        }
        String username = tokenProvider.getUsernameFromToken(refreshToken);
        var user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.status(401).build();
        String accessToken = tokenProvider.generateAccessToken(user);
        String idToken = tokenProvider.generateIdToken(user);
        String newRefresh = tokenProvider.generateRefreshToken(user);
        var roles = user.getRoles().stream().map(r -> r.getName().name()).collect(java.util.stream.Collectors.toSet());
        return ResponseEntity.ok(LoginResponseDTO.builder()
                .accessToken(accessToken)
                .idToken(idToken)
                .refreshToken(newRefresh)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .build());
    }

    // OAuth2 token endpoint compatível (para clientes OAuth2 genéricos)
    @PostMapping("/oauth/token")
    public ResponseEntity<LoginResponseDTO> oauthToken(@RequestBody @Valid LoginRequestDTO dto) {
        return login(dto);
    }
}
