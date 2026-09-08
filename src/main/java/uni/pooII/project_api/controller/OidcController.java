package uni.pooII.project_api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class OidcController {

    @Value("${app.jwt.issuer:http://localhost:8080}")
    private String issuer;

    @GetMapping("/.well-known/openid-configuration")
    public ResponseEntity<Map<String, Object>> openidConfiguration() {
        Map<String, Object> cfg = new java.util.LinkedHashMap<>();
        cfg.put("issuer", issuer);
        cfg.put("authorization_endpoint", issuer + "/oauth2/authorize");
        cfg.put("token_endpoint", issuer + "/api/auth/login");
        cfg.put("userinfo_endpoint", issuer + "/oauth2/userinfo");
        cfg.put("jwks_uri", issuer + "/oauth2/jwks");
        cfg.put("scopes_supported", List.of("openid", "profile", "email"));
        cfg.put("response_types_supported", List.of("code", "token", "id_token"));
        cfg.put("subject_types_supported", List.of("public"));
        cfg.put("id_token_signing_alg_values_supported", List.of("HS256"));
        cfg.put("claims_supported", List.of("sub", "iss", "aud", "exp", "iat", "email", "roles", "name"));
        cfg.put("grant_types_supported", List.of("authorization_code", "refresh_token", "client_credentials", "password"));
        return ResponseEntity.ok(cfg);
    }

    @GetMapping("/oauth2/userinfo")
    public ResponseEntity<Map<String, Object>> userinfo(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).build();
        var authorities = userDetails.getAuthorities().stream().map(a -> a.getAuthority()).toList();
        return ResponseEntity.ok(Map.of(
                "sub", userDetails.getUsername(),
                "preferred_username", userDetails.getUsername(),
                "roles", authorities,
                "email_verified", true
        ));
    }

    @GetMapping("/oauth2/jwks")
    public ResponseEntity<Map<String, Object>> jwks() {
        // HS256 - não expõe chave; retorna info mínima para OIDC discovery
        return ResponseEntity.ok(Map.of(
                "keys", List.of(
                        Map.of(
                                "kty", "oct",
                                "alg", "HS256",
                                "use", "sig",
                                "kid", "techhub-key-1"
                        )
                )
        ));
    }
}
