package uni.pooII.project_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uni.pooII.project_api.model.Role;
import uni.pooII.project_api.model.RoleName;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
