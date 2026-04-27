package k.repository;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import k.model.Comanda;
import k.model.DocumentoFiscal;
import k.model.Empresa;

@ApplicationScoped
public class DocumentoFiscalRepository implements PanacheRepository<DocumentoFiscal> {

    public List<DocumentoFiscal> findByEmpresa(Empresa e) {
        if (e == null) {
            return List.of();
        }
        return list("empresa = ?1 and ativo = true order by emitidoEm desc", e);
    }

    public List<DocumentoFiscal> findByComanda(Comanda c) {
        if (c == null) {
            return List.of();
        }
        return list("comanda = ?1 and ativo = true order by emitidoEm desc", c);
    }
}
