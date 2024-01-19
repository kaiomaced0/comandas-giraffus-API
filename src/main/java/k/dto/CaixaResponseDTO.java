package k.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import k.model.Caixa;

public record CaixaResponseDTO(
                String nome,
                double valorTotal,
                String comentario,
                Boolean fechado

) {
        public CaixaResponseDTO(Caixa caixa) {
                this(caixa.getNome(),
                                caixa.getValorTotal(), caixa.getComentario(),
                                caixa.getFechado());
        }

}
