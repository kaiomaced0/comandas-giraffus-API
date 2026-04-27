package k.dto;

import java.math.BigDecimal;

public record MovimentoCaixaInputDTO(
        BigDecimal valor,
        String motivo) {
}
