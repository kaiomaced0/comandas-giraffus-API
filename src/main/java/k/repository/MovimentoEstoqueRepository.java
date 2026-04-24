package k.repository;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import k.model.MovimentoEstoque;
import k.model.Produto;

@ApplicationScoped
public class MovimentoEstoqueRepository implements PanacheRepository<MovimentoEstoque> {

    public List<MovimentoEstoque> findByProduto(Produto produto) {
        return find("produto = ?1", produto).list();
    }
}
