package k.dto;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import k.model.FormaPagamento;
import k.model.Pagamento;
import k.model.enums.ModoPagamento;

public record PagamentoResponseDTO(
        Long id,
        Long idComanda,
        Boolean pagamentoRealizado,
        Boolean estornado,
        FormaPagamento formaPagamento,
        ModoPagamento modo,
        Long idUsuarioCaixa,
        Long idCaixa,
        BigDecimal valorPagamento,
        BigDecimal valorTotal,
        BigDecimal valorGorjeta,
        List<PagamentoItemResponseDTO> itens) {

    public PagamentoResponseDTO(Pagamento p) {
        this(p.getId(),
                p.getComanda() == null ? null : p.getComanda().getId(),
                p.getPagamentoRealizado(),
                p.getEstornado(),
                p.getFormaPagamento(),
                p.getModo(),
                p.getUsuarioCaixa() == null ? null : p.getUsuarioCaixa().getId(),
                p.getCaixa() == null ? null : p.getCaixa().getId(),
                p.getValorPagamento(),
                p.getValorTotal(),
                p.getValorGorjeta(),
                p.getItens() == null
                        ? Collections.emptyList()
                        : p.getItens().stream().map(PagamentoItemResponseDTO::new).collect(Collectors.toList()));
    }
}
