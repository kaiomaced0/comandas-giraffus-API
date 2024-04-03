package k.dto;

import java.util.List;
import java.util.stream.Collectors;

import k.model.Pedido;
import k.model.StatusPedido;

public record PedidoResponseDTO(
                Long idPedido,
                String nomeComanda,
                List<ItemCompraResponseDTO> listaItemCompra,
                String observacao,
                String status,
                Integer quantidadePessoas,
                Double valor) {
        public PedidoResponseDTO(Pedido pedido) {
                this(pedido.getId(), pedido.getComanda().getNome(), (pedido.getItemCompras().stream()
                                .map(itemCompra -> new ItemCompraResponseDTO(itemCompra.getId(),itemCompra.getProduto().getId(),
                                                itemCompra.getProduto().getNome(),
                                                itemCompra.getQuantidade(), itemCompra.getPreco()))
                                .collect(Collectors.toList())), pedido.getObservacao(), pedido.getStatusPedido().getLabel(),
                                pedido.getQuantidadePessoas(), pedido.getValor());
        }

}
