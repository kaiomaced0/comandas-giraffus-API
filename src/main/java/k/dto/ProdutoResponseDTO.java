package k.dto;

import k.model.Produto;

public record ProdutoResponseDTO(
        Long id,
        String nome,
        Double valorVenda,
        Integer estoque) {

    public ProdutoResponseDTO(Produto produto) {
        this(produto.getId(),
                produto.getNome(),
                produto.getValorVenda(),
                produto.getEstoque());
    }

}