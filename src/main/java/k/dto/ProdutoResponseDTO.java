package k.dto;

import k.model.Produto;

public record ProdutoResponseDTO(
        Long id,
        String nome,
        String descricao,
        String linkimage,
        Double custo,
        Double valor,
        Integer estoque) {

    public ProdutoResponseDTO(Produto produto) {
        this(produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getLinkimage(),
                produto.getValorCompra(),
                produto.getValorVenda(),
                produto.getEstoque());
    }

}