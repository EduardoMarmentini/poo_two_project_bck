package uni.pooII.project_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uni.pooII.project_api.model.Mercadoria;

import java.util.List;

public interface MercadoriaRepository extends JpaRepository<Mercadoria, Long> {

    List<Mercadoria> findByQuantidadeGreaterThan(Integer quantidade);

    List<Mercadoria> findByFornecedorId(Long fornecedorId);
}