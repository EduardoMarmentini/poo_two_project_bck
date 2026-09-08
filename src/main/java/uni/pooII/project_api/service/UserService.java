package uni.pooII.project_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uni.pooII.project_api.dto.auth.UserResponseDTO;
import uni.pooII.project_api.exception.BadRequestException;
import uni.pooII.project_api.exception.NotFoundException;
import uni.pooII.project_api.model.RoleName;
import uni.pooII.project_api.repository.RoleRepository;
import uni.pooII.project_api.repository.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponseDTO> listar() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    public UserResponseDTO buscarPorId(Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
        return toResponse(user);
    }

    public void deletar(Long id) {
        if (!userRepository.existsById(id)) throw new NotFoundException("Usuário não encontrado");
        userRepository.deleteById(id);
    }

    public UserResponseDTO atualizarRoles(Long id, Set<String> rolesStr) {
        var user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
        Set<uni.pooII.project_api.model.Role> roles = rolesStr.stream()
                .map(r -> {
                    RoleName rn = RoleName.valueOf(r);
                    return roleRepository.findByName(rn).orElseThrow(() -> new BadRequestException("Role não encontrada: " + r));
                }).collect(Collectors.toSet());
        user.setRoles(roles);
        return toResponse(userRepository.save(user));
    }

    private UserResponseDTO toResponse(uni.pooII.project_api.model.User user) {
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
