package uni.pooII.project_api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import uni.pooII.project_api.model.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expirationMs;
    private final long refreshExpirationMs;
    private final String issuer;

    public JwtTokenProvider(
            @Value("${app.jwt.secret:5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437}") String secret,
            @Value("${app.jwt.expiration-ms:3600000}") long expirationMs,
            @Value("${app.jwt.refresh-expiration-ms:86400000}") long refreshExpirationMs,
            @Value("${app.jwt.issuer:http://localhost:8080}") String issuer) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
        this.issuer = issuer;
    }

    public String generateAccessToken(User user) {
        Set<String> roles = user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet());
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .subject(user.getUsername())
                .issuer(issuer)
                .audience().add("techhub-client").and()
                .claim("email", user.getEmail())
                .claim("roles", roles)
                .claim("userId", user.getId())
                .issuedAt(now)
                .expiration(expiry)
                .claim("scope", "openid profile email")
                .signWith(key)
                .compact();
    }

    public String generateIdToken(User user) {
        Set<String> roles = user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet());
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .subject(user.getUsername())
                .issuer(issuer)
                .audience().add("techhub-client").and()
                .claim("email", user.getEmail())
                .claim("email_verified", true)
                .claim("name", user.getUsername())
                .claim("roles", roles)
                .claim("nonce", null)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshExpirationMs);
        return Jwts.builder()
                .subject(user.getUsername())
                .issuer(issuer)
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return parse(token).getPayload().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
    }

    public SecretKey getKey() { return key; }
}
