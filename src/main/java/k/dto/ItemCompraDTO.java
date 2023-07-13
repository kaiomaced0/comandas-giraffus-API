// package k.dto;

// import jakarta.validation.constraints.NotBlank;
// import k.model.ItemCompra;
// import k.resource.ProdutoResource;

// public record ItemCompraDTO(
//         @NotBlank Long produtoId,
//         @NotBlank Integer quantidade) {

//     public static ItemCompra criaItemCompra(ItemCompraDTO itemCompraDTO) {
//         try {

//             ItemCompra i = new ItemCompra();
//             ProdutoResource p = new ProdutoResource();

//             i.setProduto(p.getId(itemCompraDTO.produtoId));
//             if (i.getProduto() == null) {
//                 throw new Exception();
//             }
//             i.setQuantidade(itemCompraDTO.quantidade);
//             i.setPreco(i.getProduto().getPreco() * itemCompraDTO.quantidade);

//             return i;
//         } catch (Exception e) {
//             return null;
//         }

//     }

// }
