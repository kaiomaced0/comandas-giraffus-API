package k.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import k.model.DocumentoFiscal;

@ApplicationScoped
public class DocumentoFiscalRepository implements PanacheRepository<DocumentoFiscal> {
}
