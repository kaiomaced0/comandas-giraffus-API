package k.dto;

import java.util.List;
import java.util.stream.Collectors;

import k.model.Pedido;
import k.model.StatusPedido;

public record PedidoResponseDTO(
        List<ItemCompraDTO> listaItemCompra,
        String observacao,
        StatusPedido statusPedido,
        Integer quantidadePessoas) {
    public PedidoResponseDTO(Pedido pedido) {
        this((pedido.getItemCompras().stream()
                .map(itemCompra -> new ItemCompraDTO(itemCompra.getProduto().getId(), itemCompra.getQuantidade()))
                .collect(Collectors.toList())), pedido.getObservacao(), pedido.getStatusPedido(),
                pedido.getQuantidadePessoas());
    }

}
