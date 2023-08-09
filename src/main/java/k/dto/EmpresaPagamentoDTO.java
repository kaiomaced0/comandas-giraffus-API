package k.dto;

import k.model.EmpresaPagamento;

public record EmpresaPagamentoDTO(
        String data,
        Boolean pago,
        Double valor,
        Long idEmpresa) {
    public static EmpresaPagamento criaEmpresaPagamento(EmpresaPagamentoDTO empresaPagamentoDTO) {
        EmpresaPagamento entity = new EmpresaPagamento();
        entity.setPago(empresaPagamentoDTO.pago());
        entity.setValor(empresaPagamentoDTO.valor());
        return entity;
    }

}
