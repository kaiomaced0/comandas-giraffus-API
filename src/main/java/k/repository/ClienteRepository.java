package k.repository;

import java.util.List;
import java.util.Optional;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import k.model.Cliente;
import k.model.Empresa;

@ApplicationScoped
public class ClienteRepository implements PanacheRepository<Cliente> {

    public Optional<Cliente> findByEmpresaAndCpf(Empresa e, String cpf) {
        if (e == null || cpf == null || cpf.isBlank()) {
            return Optional.empty();
        }
        return find("empresa = ?1 and cpf = ?2 and ativo = true", e, cpf).firstResultOptional();
    }

    public List<Cliente> findByEmpresa(Empresa e) {
        if (e == null)
            return List.of();
        return list("empresa = ?1 and ativo = true", e);
    }
}
