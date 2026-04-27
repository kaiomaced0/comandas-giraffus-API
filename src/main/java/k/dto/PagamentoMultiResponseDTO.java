package k.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import k.model.Pagamento;

public record PagamentoMultiResponseDTO(
        Long id,
        Long comandaId,
        String modo,
        String formaPagamento,
        BigDecimal valorTotal,
        BigDecimal valorGorjeta,
        Boolean estornado,
        Long caixaId,
        LocalDate data,
        List<PagamentoItemResponseDTO> itens) {

    public PagamentoMultiResponseDTO(Pagamento p) {
        this(p.getId(),
                p.getComanda() == null ? null : p.getComanda().getId(),
                p.getModo() == null ? null : p.getModo().name(),
                p.getFormaPagamento() == null ? null : p.getFormaPagamento().name(),
                p.getValorTotal(),
                p.getValorGorjeta() == null ? null : BigDecimal.valueOf(p.getValorGorjeta()),
                p.getEstornado(),
                p.getCaixa() == null ? null : p.getCaixa().getId(),
                p.getDataInclusao() == null ? null : p.getDataInclusao().toLocalDate(),
                p.getItens() == null ? List.of()
                        : p.getItens().stream().map(PagamentoItemResponseDTO::new).collect(Collectors.toList()));
    }
}
