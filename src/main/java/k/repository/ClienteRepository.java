package k.repository;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import k.model.Cliente;
import k.model.Empresa;

@ApplicationScoped
public class ClienteRepository implements PanacheRepository<Cliente> {

    public Cliente findByEmpresaAndCpf(Empresa empresa, String cpf) {
        if (empresa == null || cpf == null) {
            return null;
        }
        return find("empresa = ?1 and cpf = ?2", empresa, cpf).firstResult();
    }

    public List<Cliente> findByEmpresa(Empresa empresa) {
        return find("empresa = ?1", empresa).list();
    }
}
