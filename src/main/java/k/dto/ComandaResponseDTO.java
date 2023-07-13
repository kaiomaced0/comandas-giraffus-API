// package k.dto;

// import java.util.List;
// import java.util.stream.Collectors;

// import k.model.Comanda;
// import k.model.Pedido;

// public record ComandaResponseDTO(
//         String nome,
//         List<Pedido> pedidos,
//         Double preco) {

//     public ComandaResponseDTO(Comanda comanda){
//         //comanda.getProdutos().stream().map(
//         //        itemCompra -> new ItemCompraDTO(itemCompra.getProduto().getId(), itemCompra.getQuantidade()))
//         this(comanda.getNome(), comanda.getPedidos(),
//         comanda.getPreco());
//     }

// }