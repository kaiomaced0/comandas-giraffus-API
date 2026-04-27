package k.dto;

import java.math.BigDecimal;

public record CaixaAbrirInputDTO(
        BigDecimal valorAbertura,
        String observacao) {
}
