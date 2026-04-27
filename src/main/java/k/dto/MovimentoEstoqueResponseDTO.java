package k.dto;

import java.time.LocalDate;

public record MovimentoEstoqueResponseDTO(
        Long id,
        Long produtoId,
        String tipo,
        Integer quantidade,
        String motivo,
        LocalDate data) {
}
