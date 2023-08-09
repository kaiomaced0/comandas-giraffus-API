package k.dto;

import k.model.FormaPagamento;

public record PagamentoDTO(
        Long idComanda,
        FormaPagamento formaPagamento,
        Long idUsuarioCaixa,
        Double valorPagamento,
        Boolean taxaServico) {

}
