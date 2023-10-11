package k.dto;

import k.model.Produto;

public record ProdutoSistemaResponseDTO(
        Long id,
        String nome,
        Double valorCompra,
        Double valorVenda,
        Integer estoque) {

    public ProdutoSistemaResponseDTO(Produto produto) {

        this(
                produto.getId(),
                produto.getNome(),
                produto.getValorCompra(),
                produto.getValorVenda(),
                produto.getEstoque());
    }

}
