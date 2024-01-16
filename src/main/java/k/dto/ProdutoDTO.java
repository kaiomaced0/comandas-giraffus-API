package k.dto;

import jakarta.validation.constraints.NotBlank;
import k.model.Produto;

public record ProdutoDTO(
        @NotBlank String nome,
        @NotBlank String descricao,
        @NotBlank String linkimage,
        @NotBlank Double custo,
        @NotBlank Double valor,
        @NotBlank Integer estoque,
        @NotBlank Long idTipoProduto) {
    public static Produto criaProduto(ProdutoDTO produtoDTO) {
        Produto p = new Produto();
        p.setNome(produtoDTO.nome);
        p.setDescricao(produtoDTO.descricao);
        p.setLinkimage(produtoDTO.linkimage);
        p.setValorCompra(produtoDTO.custo());
        p.setValorVenda(produtoDTO.valor());
        p.setEstoque(produtoDTO.estoque);

        return p;
    }

}
