package k.dto;

import k.model.Produto;

public record ProdutoSistemaResponseDTO(
        Long id,
        String nome,
        String descricao,
        Double custo,
        Double valor,
        Integer estoque) {

    public ProdutoSistemaResponseDTO(Produto produto) {

        this(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getValorCompra(),
                produto.getValorVenda(),
                produto.getEstoque());
    }

}
