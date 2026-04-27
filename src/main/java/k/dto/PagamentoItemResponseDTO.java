package k.dto;

import java.math.BigDecimal;

import k.model.PagamentoItem;

public record PagamentoItemResponseDTO(
        Long id,
        Long itemCompraId,
        Integer quantidade,
        BigDecimal valorAbatido) {

    public PagamentoItemResponseDTO(PagamentoItem item) {
        this(item.getId(),
                item.getItemCompra() == null ? null : item.getItemCompra().getId(),
                item.getQuantidade(),
                item.getValorAbatido());
    }
}
