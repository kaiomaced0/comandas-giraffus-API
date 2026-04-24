package k.dto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import k.model.Comanda;

public record ComandaResponseDTO(
        Long id,
        String nome,
        List<PedidoResponseDTO> pedidos,
        Double preco,
        Long idAtendente,
        Long mesaId,
        Boolean finalizada) {

    public ComandaResponseDTO(Comanda comanda) {
        this(comanda.getId(),
                comanda.getNome(),
                comanda.getPedidos() == null
                        ? Collections.emptyList()
                        : comanda.getPedidos().stream()
                                .map(PedidoResponseDTO::new)
                                .collect(Collectors.toList()),
                comanda.getPreco(),
                comanda.getAtendente() == null ? null : comanda.getAtendente().getId(),
                comanda.getMesa() == null ? null : comanda.getMesa().getId(),
                comanda.getFinalizada());
    }
}
