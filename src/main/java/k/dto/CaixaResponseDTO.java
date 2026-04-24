package k.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import k.model.Caixa;

public record CaixaResponseDTO(
        Long id,
        String nome,
        Long usuarioId,
        BigDecimal valorTotal,
        BigDecimal valorAbertura,
        BigDecimal valorFechamentoEsperado,
        BigDecimal valorFechamentoInformado,
        BigDecimal diferenca,
        LocalDateTime horaAbertura,
        LocalDateTime horaFechamento,
        String observacoesFechamento,
        String comentario,
        Boolean fechado) {

    public CaixaResponseDTO(Caixa caixa) {
        this(caixa.getId(),
                caixa.getNome(),
                caixa.getUsuario() != null ? caixa.getUsuario().getId() : null,
                caixa.getValorTotal(),
                caixa.getValorAbertura(),
                caixa.getValorFechamentoEsperado(),
                caixa.getValorFechamentoInformado(),
                caixa.getDiferenca(),
                caixa.getHoraAbertura(),
                caixa.getHoraFechamento(),
                caixa.getObservacoesFechamento(),
                caixa.getComentario(),
                caixa.getFechado());
    }
}
