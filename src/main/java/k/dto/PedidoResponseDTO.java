package k.dto;

import java.util.List;
import java.util.stream.Collectors;

import k.model.Pedido;
import k.model.StatusPedido;

public record PedidoResponseDTO(
                List<ItemCompraResponseDTO> listaItemCompra,
                String observacao,
                StatusPedido statusPedido,
                Integer quantidadePessoas) {
        public PedidoResponseDTO(Pedido pedido) {
                this((pedido.getItemCompras().stream()
                                .map(itemCompra -> new ItemCompraResponseDTO(itemCompra.getProduto().getId(),
                                                itemCompra.getProduto().getNome(),
                                                itemCompra.getQuantidade(), itemCompra.getPreco()))
                                .collect(Collectors.toList())), pedido.getObservacao(), pedido.getStatusPedido(),
                                pedido.getQuantidadePessoas());
        }

}
