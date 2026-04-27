package k.repository;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import k.model.Comanda;
import k.model.Pagamento;

@ApplicationScoped
public class PagamentoRepository implements PanacheRepository<Pagamento> {

    public List<Pagamento> findByComanda(Comanda comanda) {
        if (comanda == null) {
            return List.of();
        }
        return find("comanda = ?1 and ativo = true", comanda).list();
    }
}
