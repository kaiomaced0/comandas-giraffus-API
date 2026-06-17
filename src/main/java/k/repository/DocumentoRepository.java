package k.repository;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import k.model.Documento;
import k.model.Empresa;

@ApplicationScoped
public class DocumentoRepository implements PanacheRepository<Documento> {

    public List<Documento> findByEmpresa(Empresa e) {
        if (e == null)
            return List.of();
        return list("empresa = ?1 and ativo = true", e);
    }
}
