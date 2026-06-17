package k.repository;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * Comandas ativas cujos ids pertencem à empresa (Comanda não tem FK curta
     * p/ empresa — derivamos os ids da coleção empresa.comandas) e que estão
     * no período informado (em dataInclusao). from/to podem ser null.
     * Isolado em método próprio para permitir override em testes.
     */
    public List<Comanda> findParaRelatorio(List<Long> idsEmpresa, LocalDateTime from, LocalDateTime to) {
        if (idsEmpresa == null || idsEmpresa.isEmpty()) {
            return List.of();
        }
        StringBuilder ql = new StringBuilder("id in :ids and ativo = true");
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("ids", idsEmpresa);
        if (from != null) {
            ql.append(" and dataInclusao >= :from");
            params.put("from", from);
        }
        if (to != null) {
            ql.append(" and dataInclusao <= :to");
            params.put("to", to);
        }
        return find(ql.toString(), params).list();
    }

}
