package k.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import k.model.Caixa;

public record CaixaResponseDTO(
                Long id,
                String nome,
                Double valorTotal,
                String comentario,
                Boolean fechado,
                Long usuarioId,
                LocalDate dataCaixa,
                BigDecimal valorAbertura,
                BigDecimal valorFechamentoEsperado,
                BigDecimal valorFechamentoInformado,
                BigDecimal diferenca,
                LocalDateTime horaAbertura,
                LocalDateTime horaFechamento,
                String observacoesFechamento,
                Long fechadoPorId

) {
        public CaixaResponseDTO(Caixa caixa) {
                this(
                                caixa.getId(),
                                caixa.getNome(),
                                caixa.getValorTotal(),
                                caixa.getComentario(),
                                caixa.getFechado(),
                                caixa.getUsuario() == null ? null : caixa.getUsuario().getId(),
                                caixa.getDataCaixa(),
                                caixa.getValorAbertura(),
                                caixa.getValorFechamentoEsperado(),
                                caixa.getValorFechamentoInformado(),
                                caixa.getDiferenca(),
                                caixa.getHoraAbertura(),
                                caixa.getHoraFechamento(),
                                caixa.getObservacoesFechamento(),
                                caixa.getFechadoPor() == null ? null : caixa.getFechadoPor().getId());
        }

}
