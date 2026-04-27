package k.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovimentoCaixaResponseDTO(
        Long id,
        String tipo,
        BigDecimal valor,
        String motivo,
        Long caixaId,
        Long caixaDestinoId,
        LocalDate data) {
}
