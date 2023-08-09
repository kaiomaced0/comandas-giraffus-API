package k.repository;

import jakarta.enterprise.context.ApplicationScoped;
import k.model.Comanda;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class ComandaRepository implements PanacheRepository<Comanda> {

    public Comanda findByNome(String nome) {
        if (nome == null)
            return null;
        return find("UPPER(nome) LIKE ?1 ", "%" + nome.toUpperCase() + "%").firstResult();
    }

}
