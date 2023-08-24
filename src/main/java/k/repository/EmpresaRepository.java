package k.repository;

import jakarta.enterprise.context.ApplicationScoped;
import k.model.Empresa;

import java.util.List;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class EmpresaRepository implements PanacheRepository<Empresa> {
    public List<Empresa> findByNome(String nome) {
        if (nome == null)
            return null;
        return find("UPPER(nome) LIKE ?1 ", "%" + nome.toUpperCase() + "%").list();
    }

    public List<Empresa> findByCnpj(String cnpj) {
        if (cnpj == null)
            return null;
        return find("UPPER(cnpj) LIKE ?1 ", "%" + cnpj.toUpperCase() + "%").list();
    }
}
