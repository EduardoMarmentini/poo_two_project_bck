package uni.pooII.project_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uni.pooII.project_api.model.Fornecedor;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
}