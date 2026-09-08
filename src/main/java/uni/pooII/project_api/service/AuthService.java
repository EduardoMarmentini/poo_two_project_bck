package uni.pooII.project_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uni.pooII.project_api.dto.auth.*;
import uni.pooII.project_api.exception.BadRequestException;
import uni.pooII.project_api.model.RoleName;
import uni.pooII.project_api.model.User;
import uni.pooII.project_api.repository.RoleRepository;
import uni.pooII.project_api.repository.UserRepository;
import uni.pooII.project_api.security.JwtTokenProvider;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public LoginResponseDTO login(LoginRequestDTO dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new BadRequestException("Usuário não encontrado"));

        String accessToken = tokenProvider.generateAccessToken(user);
        String idToken = tokenProvider.generateIdToken(user);
        String refreshToken = tokenProvider.generateRefreshToken(user);

        Set<String> roles = user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet());

        return LoginResponseDTO.builder()
                .accessToken(accessToken)
                .idToken(idToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .build();
    }

    public UserResponseDTO register(RegisterRequestDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BadRequestException("Username já existe");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email já existe");
        }

        Set<uni.pooII.project_api.model.Role> roles = dto.getRoles().stream()
                .map(r -> {
                    try {
                        RoleName rn = RoleName.valueOf(r);
                        return roleRepository.findByName(rn)
                                .orElseThrow(() -> new BadRequestException("Role não encontrada: " + r));
                    } catch (IllegalArgumentException e) {
                        throw new BadRequestException("Role inválida: " + r);
                    }
                })
                .collect(Collectors.toSet());

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .roles(roles)
                .enabled(true)
                .build();

        user = userRepository.save(user);

        return toResponse(user);
    }

    public UserResponseDTO me(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("Usuário não encontrado"));
        return toResponse(user);
    }

    private UserResponseDTO toResponse(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()))
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
