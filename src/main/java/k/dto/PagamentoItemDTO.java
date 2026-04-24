package k.dto;

import java.math.BigDecimal;

public record PagamentoItemDTO(
        Long itemCompraId,
        Integer quantidade,
        BigDecimal valorAbatido) {
}
