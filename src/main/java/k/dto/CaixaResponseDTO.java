package k.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import k.model.Caixa;

public record CaixaResponseDTO(
        List<Long> idComandas,
        List<Long> idPagamentos,
        double valorTotal,
        LocalDate dataCaixa,
        String comentario,
        boolean fechado

) {
    public CaixaResponseDTO(Caixa caixa) {
        this(caixa.getComandas().stream().map(comanda -> comanda.getId()).collect(Collectors.toList()),
                caixa.getPagamentos().stream().map(pagamento -> pagamento.getId())
                        .collect(Collectors.toList()), caixa.getValorTotal(), caixa.getDataCaixa(), caixa.getComentario(),
                caixa.getFechado());
    }

}
