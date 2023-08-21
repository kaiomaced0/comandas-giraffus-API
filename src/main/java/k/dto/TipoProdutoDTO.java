package k.dto;

import k.model.TipoProduto;

public record TipoProdutoDTO(
        String nome) {
    public static TipoProduto criaTipoProduto(TipoProdutoDTO tipoProdutoDTO) {
        TipoProduto tipoProduto = new TipoProduto();
        tipoProduto.setNome(tipoProdutoDTO.nome());
        return tipoProduto;
    }

}
