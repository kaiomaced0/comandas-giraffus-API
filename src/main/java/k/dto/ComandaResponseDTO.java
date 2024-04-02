package k.dto;

import java.util.List;
import java.util.stream.Collectors;

import k.model.Comanda;

public record ComandaResponseDTO(
        Long id,
        String nome,
        List<PedidoResponseDTO> pedidos,
        Double preco,
        Long idAtendente,
        Boolean finalizada) {

    public ComandaResponseDTO(Comanda comanda) {
        this(comanda.getId(), comanda.getNome(),
                comanda.getPedidos().stream().map(PedidoResponseDTO::new).collect(Collectors.toList()),
                comanda.getPreco(), comanda.getAtendente().getId(), comanda.getFinalizada());
    }

}