package k.repository;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import k.model.ItemCompra;

@ApplicationScoped
public class ItemCompraRepository implements PanacheRepository<ItemCompra> {

    /**
     * Itens de compra ativos pertencentes às comandas informadas (via
     * pedido.comanda.id in :ids), no período (em dataInclusao). Usado pelo
     * relatório de top-produtos. from/to podem ser null. Isolado em método
     * próprio para permitir override em testes.
     */
    public List<ItemCompra> findParaRelatorio(List<Long> idsComanda, LocalDateTime from, LocalDateTime to) {
        if (idsComanda == null || idsComanda.isEmpty()) {
            return List.of();
        }
        StringBuilder ql = new StringBuilder("pedido.comanda.id in :ids and ativo = true");
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("ids", idsComanda);
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
