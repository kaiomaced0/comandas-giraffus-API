package k.dto;

import java.math.BigDecimal;

public record CaixaForcadoFecharInputDTO(
        BigDecimal valorFechamentoInformado,
        String observacoesFechamento,
        String justificativa) {
}
