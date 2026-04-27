package k.repository;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import k.model.Caixa;
import k.model.MovimentoCaixa;

@ApplicationScoped
public class MovimentoCaixaRepository implements PanacheRepository<MovimentoCaixa> {

    public List<MovimentoCaixa> findByCaixa(Caixa c) {
        if (c == null)
            return List.of();
        return list("caixa = ?1 and ativo = true order by dataInclusao desc", c);
    }
}
