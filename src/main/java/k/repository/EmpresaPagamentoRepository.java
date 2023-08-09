package k.repository;

import jakarta.enterprise.context.ApplicationScoped;
import k.model.EmpresaPagamento;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class EmpresaPagamentoRepository implements PanacheRepository<EmpresaPagamento> {
  
}
