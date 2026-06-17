package k.repository;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import k.model.Comanda;
import k.model.Empresa;
import k.model.Pagamento;

@ApplicationScoped
public class PagamentoRepository implements PanacheRepository<Pagamento> {

    public List<Pagamento> findByComanda(Comanda comanda) {
        if (comanda == null) {
            return List.of();
        }
        return find("comanda = ?1 and ativo = true", comanda).list();
    }

    /**
     * Pagamentos ativos, não estornados, da empresa informada, no período
     * (em dataInclusao). from/to podem ser null (sem limite). Usado pelos
     * relatórios/dashboard; isolado em método próprio para permitir override
     * em testes com fake retornando lista em memória.
     */
    public List<Pagamento> findParaRelatorio(Empresa empresa, LocalDateTime from, LocalDateTime to) {
        if (empresa == null) {
            return List.of();
        }
        StringBuilder ql = new StringBuilder(
                "usuarioCaixa.empresa = :empresa and ativo = true and (estornado is null or estornado = false)");
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("empresa", empresa);
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
