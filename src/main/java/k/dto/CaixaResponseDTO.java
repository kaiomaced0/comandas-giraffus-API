package k.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import k.model.Caixa;

public record CaixaResponseDTO(
                Long id,
                String nome,
                Double valorTotal,
                String comentario,
                Boolean fechado

) {
        public CaixaResponseDTO(Caixa caixa) {
                this(           caixa.getId(), caixa.getNome(),
                                caixa.getValorTotal(), caixa.getComentario(),
                                caixa.getFechado());
        }

}
