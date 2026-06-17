package k.dto;

import java.math.BigDecimal;

public record DashboardKpisDTO(
        BigDecimal faturamento,
        long numPagamentos,
        BigDecimal ticketMedio,
        BigDecimal totalGorjetas,
        long comandasAbertas,
        long comandasFinalizadas) {
}
