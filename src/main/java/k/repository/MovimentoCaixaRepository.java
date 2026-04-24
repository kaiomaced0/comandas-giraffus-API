package k.repository;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import k.model.Caixa;
import k.model.MovimentoCaixa;

@ApplicationScoped
public class MovimentoCaixaRepository implements PanacheRepository<MovimentoCaixa> {

    public List<MovimentoCaixa> findByCaixa(Caixa caixa) {
        return find("caixa = ?1 or caixaDestino = ?1", caixa).list();
    }
}
