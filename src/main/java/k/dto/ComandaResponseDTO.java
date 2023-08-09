package k.dto;

import java.util.List;
import java.util.stream.Collectors;

import k.model.Comanda;

public record ComandaResponseDTO(
        String nome,
        List<Long> idPedidos,
        Double preco,
        Long idAtendente,
        Boolean finalizada) {

    public ComandaResponseDTO(Comanda comanda) {
        this(comanda.getNome(),
                comanda.getPedidos().stream().map(pedido -> pedido.getId()).collect(Collectors.toList()),
                comanda.getPreco(), comanda.getAtendente().getId(), comanda.getFinalizada());
    }

}