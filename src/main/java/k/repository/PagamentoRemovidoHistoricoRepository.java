package k.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import k.model.PagamentoRemovidoHistorico;

@ApplicationScoped
public class PagamentoRemovidoHistoricoRepository implements PanacheRepository<PagamentoRemovidoHistorico> {

}
