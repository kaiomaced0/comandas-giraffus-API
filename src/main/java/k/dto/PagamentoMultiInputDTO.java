package k.dto;

import java.math.BigDecimal;
import java.util.List;

public record PagamentoMultiInputDTO(
        String modo,
        Integer idFormaPagamento,
        BigDecimal valorTotal,
        Boolean taxaServico,
        List<PagamentoItemInputDTO> itens) {
}
