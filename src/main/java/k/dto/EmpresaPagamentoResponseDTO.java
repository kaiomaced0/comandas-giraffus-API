package k.dto;

import k.model.EmpresaPagamento;

public record EmpresaPagamentoResponseDTO(
        String data,
        Boolean pago,
        Double valor) {
    public EmpresaPagamentoResponseDTO(EmpresaPagamento empresaPagamento) {
        this(
                empresaPagamento.getData(), empresaPagamento.getPago(), empresaPagamento.getValor());
    }

}
