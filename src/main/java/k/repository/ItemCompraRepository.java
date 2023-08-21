package k.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import k.model.ItemCompra;
@ApplicationScoped
public class ItemCompraRepository implements PanacheRepository<ItemCompra> {

}
