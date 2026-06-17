package k.repository;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import k.model.Empresa;
import k.model.EmpresaGatewayConfig;

@ApplicationScoped
public class EmpresaGatewayConfigRepository implements PanacheRepository<EmpresaGatewayConfig> {

    public List<EmpresaGatewayConfig> findByEmpresa(Empresa e) {
        if (e == null)
            return List.of();
        return list("empresa = ?1 and ativo = true", e);
    }
}
