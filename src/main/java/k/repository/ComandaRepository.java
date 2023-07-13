package k.repository;

import jakarta.enterprise.context.ApplicationScoped;
import k.model.Comanda;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class ComandaRepository implements PanacheRepository<Comanda>{
    
}
