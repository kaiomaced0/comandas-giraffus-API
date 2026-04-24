package k.dto;

import java.math.BigDecimal;

public record AbrirCaixaDTO(
        BigDecimal valorAbertura,
        String nome,
        String comentario) {
}
