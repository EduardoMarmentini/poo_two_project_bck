package uni.pooII.project_api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import uni.pooII.project_api.model.Role;
import uni.pooII.project_api.model.RoleName;
import uni.pooII.project_api.model.User;
import uni.pooII.project_api.repository.RoleRepository;
import uni.pooII.project_api.repository.UserRepository;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initData() {
        return args -> {
            // Cria roles se não existirem
            for (RoleName rn : RoleName.values()) {
                if (roleRepository.findByName(rn).isEmpty()) {
                    roleRepository.save(Role.builder().name(rn).description(rn.name()).build());
                    System.out.println("[INIT] Role criada: " + rn);
                }
            }

            // Cria usuários padrão se não existirem
            createUserIfNotExists("admin", "admin@techhub.local", "123456", Set.of(RoleName.SYSTEM_ADMIN));
            createUserIfNotExists("manager", "manager@techhub.local", "123456", Set.of(RoleName.SYSTEM_MANAGER));
            createUserIfNotExists("user", "user@techhub.local", "123456", Set.of(RoleName.SYSTEM_USER));
        };
    }

    private void createUserIfNotExists(String username, String email, String rawPassword, Set<RoleName> roleNames) {
        if (userRepository.existsByUsername(username)) return;
        Set<Role> roles = roleNames.stream()
                .map(rn -> roleRepository.findByName(rn).orElseThrow())
                .collect(java.util.stream.Collectors.toSet());
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .roles(roles)
                .enabled(true)
                .build();
        userRepository.save(user);
        System.out.println("[INIT] Usuário criado: " + username + " -> " + roleNames);
    }
}
