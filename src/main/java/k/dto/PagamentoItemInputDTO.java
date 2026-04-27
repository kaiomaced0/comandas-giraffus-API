package k.dto;

import java.math.BigDecimal;

public record PagamentoItemInputDTO(
        Long itemCompraId,
        Integer quantidade,
        BigDecimal valorAbatido) {
}
