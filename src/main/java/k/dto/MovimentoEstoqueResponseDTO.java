package k.dto;

import java.time.LocalDateTime;

import k.model.MovimentoEstoque;
import k.model.enums.TipoMovimentoEstoque;

public record MovimentoEstoqueResponseDTO(
        Long id,
        Long produtoId,
        TipoMovimentoEstoque tipo,
        Integer quantidade,
        String motivo,
        Long usuarioId,
        LocalDateTime data) {

    public MovimentoEstoqueResponseDTO(MovimentoEstoque m) {
        this(m.getId(),
                m.getProduto() == null ? null : m.getProduto().getId(),
                m.getTipo(),
                m.getQuantidade(),
                m.getMotivo(),
                m.getUsuario() == null ? null : m.getUsuario().getId(),
                m.getData());
    }
}
