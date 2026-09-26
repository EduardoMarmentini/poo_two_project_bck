package uni.pooII.project_api.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    @SuppressWarnings("java:S4502")
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> {})
            .csrf(csrf -> csrf.requireCsrfProtectionMatcher(request -> {
                String path = request.getRequestURI();
                return !(path.startsWith("/api/")
                    || path.startsWith("/fornecedores/")
                    || path.startsWith("/mercadorias/")
                    || path.startsWith("/oauth2/")
                    || path.startsWith("/.well-known/"));
            }))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, authEx) -> {
                    res.setStatus(401);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"status\":401,\"message\":\"Não autenticado\"}");
                })
                .accessDeniedHandler((req, res, accessEx) -> {
                    res.setStatus(403);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"status\":403,\"message\":\"Acesso negado\"}");
                })
            )
            .authorizeHttpRequests(auth -> auth
                // public auth endpoints (login, refresh, oauth). NÃO inclui /api/auth/me que deve ser autenticado
                .requestMatchers("/api/auth/login", "/api/auth/refresh", "/api/auth/oauth/**", "/api/auth/register").permitAll()
                .requestMatchers("/.well-known/**", "/oauth2/**", "/error").permitAll()
                // API com prefixo /api para funcionar atrás do nginx
                .requestMatchers(HttpMethod.GET, "/api/fornecedores/**", "/api/mercadorias/**").hasAnyAuthority("SYSTEM_ADMIN", "SYSTEM_MANAGER", "SYSTEM_USER")
                .requestMatchers(HttpMethod.POST, "/api/mercadorias/*/movimentacao").hasAnyAuthority("SYSTEM_ADMIN", "SYSTEM_MANAGER", "SYSTEM_USER")
                .requestMatchers(HttpMethod.PATCH, "/api/mercadorias/**").hasAnyAuthority("SYSTEM_ADMIN", "SYSTEM_MANAGER", "SYSTEM_USER")
                .requestMatchers(HttpMethod.POST, "/api/mercadorias/**", "/api/fornecedores/**").hasAnyAuthority("SYSTEM_ADMIN", "SYSTEM_MANAGER")
                .requestMatchers(HttpMethod.PUT, "/api/mercadorias/**", "/api/fornecedores/**").hasAnyAuthority("SYSTEM_ADMIN", "SYSTEM_MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/mercadorias/**", "/api/fornecedores/**").hasAnyAuthority("SYSTEM_ADMIN", "SYSTEM_MANAGER")
                // endpoints sem prefixo /api (acesso direto ou legado)
                .requestMatchers(HttpMethod.GET, "/mercadorias/**", "/fornecedores/**").hasAnyAuthority("SYSTEM_ADMIN", "SYSTEM_MANAGER", "SYSTEM_USER")
                .requestMatchers(HttpMethod.POST, "/mercadorias/*/movimentacao").hasAnyAuthority("SYSTEM_ADMIN", "SYSTEM_MANAGER", "SYSTEM_USER")
                .requestMatchers(HttpMethod.PATCH, "/mercadorias/**").hasAnyAuthority("SYSTEM_ADMIN", "SYSTEM_MANAGER", "SYSTEM_USER")
                .requestMatchers(HttpMethod.POST, "/mercadorias/**", "/fornecedores/**").hasAnyAuthority("SYSTEM_ADMIN", "SYSTEM_MANAGER")
                .requestMatchers(HttpMethod.PUT, "/mercadorias/**", "/fornecedores/**").hasAnyAuthority("SYSTEM_ADMIN", "SYSTEM_MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/mercadorias/**", "/fornecedores/**").hasAnyAuthority("SYSTEM_ADMIN", "SYSTEM_MANAGER")
                // gestão de usuários: apenas ADMIN
                .requestMatchers("/api/users/**").hasAuthority("SYSTEM_ADMIN")
                .anyRequest().authenticated()
            )
            .userDetailsService(userDetailsService);

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:3000", "https://localhost:3000", "https://techhub.local", "http://techhub.local", "https://techhub.local:*", "http://techhub.local:*"));
        config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
