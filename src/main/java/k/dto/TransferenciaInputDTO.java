package k.dto;

import java.math.BigDecimal;

public record TransferenciaInputDTO(
        BigDecimal valor,
        String motivo,
        Long caixaDestinoId) {
}
