package k.repository;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import k.model.ItemCompra;
import k.model.PagamentoItem;

@ApplicationScoped
public class PagamentoItemRepository implements PanacheRepository<PagamentoItem> {

    public List<PagamentoItem> findByItemCompra(ItemCompra item) {
        return find("itemCompra = ?1", item).list();
    }
}
