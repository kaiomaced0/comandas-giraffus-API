package k.dto;

import java.math.BigDecimal;

public record MovimentoCaixaDTO(
        BigDecimal valor,
        String motivo) {
}
