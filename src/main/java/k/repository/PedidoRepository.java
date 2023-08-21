package k.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import k.model.Pedido;

@ApplicationScoped
public class PedidoRepository implements PanacheRepository<Pedido> {

}
