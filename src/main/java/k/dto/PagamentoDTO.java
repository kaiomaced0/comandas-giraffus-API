package k.dto;

import java.math.BigDecimal;
import java.util.List;

public record PagamentoDTO(
        Long idComanda,
        Integer idFormaPagamento,
        Integer idModoPagamento,
        BigDecimal valorTotal,
        Boolean taxaServico,
        List<PagamentoItemDTO> itens) {
}
