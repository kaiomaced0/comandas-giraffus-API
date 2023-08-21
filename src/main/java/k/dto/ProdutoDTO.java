package k.dto;

import jakarta.validation.constraints.NotBlank;
import k.model.Produto;

public record ProdutoDTO(
        @NotBlank String nome,
        @NotBlank Double valorCompra,
        @NotBlank Double valorVenda,
        @NotBlank Integer estoque,
        @NotBlank Long idTipoProduto) {
    public static Produto criaProduto(ProdutoDTO produtoDTO) {
        Produto p = new Produto();
        p.setNome(produtoDTO.nome);
        p.setValorCompra(produtoDTO.valorCompra());
        p.setValorVenda(produtoDTO.valorVenda());
        p.setEstoque(produtoDTO.estoque);

        return p;
    }

}
