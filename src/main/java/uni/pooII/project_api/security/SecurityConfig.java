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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // public
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/.well-known/**", "/oauth2/**", "/error").permitAll()
                // GET público autenticado (todos roles)
                .requestMatchers(HttpMethod.GET, "/mercadorias/**", "/fornecedores/**").hasAnyAuthority("SYSTEM_ADMIN", "SYSTEM_MANAGER", "SYSTEM_USER")
                // Movimento de estoque: todos roles autenticados
                .requestMatchers(HttpMethod.POST, "/mercadorias/*/movimentacao").hasAnyAuthority("SYSTEM_ADMIN", "SYSTEM_MANAGER", "SYSTEM_USER")
                .requestMatchers(HttpMethod.PATCH, "/mercadorias/**").hasAnyAuthority("SYSTEM_ADMIN", "SYSTEM_MANAGER", "SYSTEM_USER")
                // escrita restrita a ADMIN e MANAGER
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
        config.setAllowedOrigins(List.of("http://localhost:3000"));
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
