package k.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import k.model.MovimentoCaixa;
import k.model.enums.TipoMovimentoCaixa;

public record MovimentoCaixaResponseDTO(
        Long id,
        TipoMovimentoCaixa tipo,
        Long caixaId,
        Long caixaDestinoId,
        BigDecimal valor,
        String motivo,
        Long usuarioId,
        LocalDateTime data) {

    public MovimentoCaixaResponseDTO(MovimentoCaixa m) {
        this(m.getId(),
                m.getTipo(),
                m.getCaixa() == null ? null : m.getCaixa().getId(),
                m.getCaixaDestino() == null ? null : m.getCaixaDestino().getId(),
                m.getValor(),
                m.getMotivo(),
                m.getUsuario() == null ? null : m.getUsuario().getId(),
                m.getData());
    }
}
