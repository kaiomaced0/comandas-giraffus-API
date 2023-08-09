package k.dto;

import k.model.Produto;


public record ProdutoDTO(
    String nome,
    Double preco,
    Integer estoque
) {
    public static Produto criaProduto(ProdutoDTO produtoDTO){
        Produto p = new Produto();
        p.setNome(produtoDTO.nome);
        p.setPreco(produtoDTO.preco);
        p.setEstoque(produtoDTO.estoque);

        return p;
    }
    
}
