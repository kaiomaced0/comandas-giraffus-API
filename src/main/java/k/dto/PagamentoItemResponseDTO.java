package k.dto;

import java.math.BigDecimal;

import k.model.PagamentoItem;

public record PagamentoItemResponseDTO(
        Long id,
        Long itemCompraId,
        Integer quantidade,
        BigDecimal valorAbatido) {

    public PagamentoItemResponseDTO(PagamentoItem pi) {
        this(pi.getId(),
                pi.getItemCompra() == null ? null : pi.getItemCompra().getId(),
                pi.getQuantidade(),
                pi.getValorAbatido());
    }
}
