package k.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import k.model.PagamentoItem;

@ApplicationScoped
public class PagamentoItemRepository implements PanacheRepository<PagamentoItem> {
}
