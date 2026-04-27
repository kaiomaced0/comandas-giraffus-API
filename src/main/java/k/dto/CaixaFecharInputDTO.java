package k.dto;

import java.math.BigDecimal;

public record CaixaFecharInputDTO(
        BigDecimal valorFechamentoInformado,
        String observacoesFechamento) {
}
