package k.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VendaPorDiaDTO(
        LocalDate dia,
        long numPagamentos,
        BigDecimal total) {
}
