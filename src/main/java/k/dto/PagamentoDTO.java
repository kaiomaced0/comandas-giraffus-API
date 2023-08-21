package k.dto;

public record PagamentoDTO(
                Long idComanda,
                Integer idFormaPagamento,
                Double valorPagamento,
                Boolean taxaServico) {

}
