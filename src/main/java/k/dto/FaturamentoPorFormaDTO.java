package k.dto;

import java.math.BigDecimal;

public record FaturamentoPorFormaDTO(
        Integer formaPagamentoId,
        String formaPagamentoLabel,
        long quantidade,
        BigDecimal total) {
}
