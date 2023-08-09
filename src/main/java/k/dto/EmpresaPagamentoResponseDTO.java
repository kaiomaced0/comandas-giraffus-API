package k.dto;

import k.model.EmpresaPagamento;

public record EmpresaPagamentoResponseDTO(
        Long idEmpresa,
        String data,
        Boolean pago,
        Double valor) {
    public EmpresaPagamentoResponseDTO(EmpresaPagamento empresaPagamento) {
        this(empresaPagamento.getEmpresa().getId(),
                empresaPagamento.getData(), empresaPagamento.getPago(), empresaPagamento.getValor());
    }

}
