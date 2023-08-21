package k.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import k.model.Pagamento;

@ApplicationScoped
public class PagamentoRepository implements PanacheRepository<Pagamento> {

}
