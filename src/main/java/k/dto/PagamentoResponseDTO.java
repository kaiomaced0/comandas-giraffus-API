package k.dto;

import k.model.FormaPagamento;

public record PagamentoResponseDTO(
        Long idComanda,
        Boolean pagamentoRealizado,
        FormaPagamento formaPagamento,
        Long idUsuarioCaixa,
        Double valorPagamento) {
}
