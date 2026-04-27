package k.dto;

import java.util.List;

public record FiscalConsolidadoInputDTO(
        String tipo,
        Long clienteId,
        List<Long> pagamentoIds) {
}
