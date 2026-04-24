package k.dto;

import java.math.BigDecimal;

public record FecharCaixaDTO(
        BigDecimal valorFechamentoInformado,
        String observacoesFechamento) {
}
