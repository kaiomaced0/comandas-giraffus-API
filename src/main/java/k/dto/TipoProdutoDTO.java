package k.dto;

import k.model.TipoProduto;

public record TipoProdutoDTO(
        String nome, String cor) {
    public static TipoProduto criaTipoProduto(TipoProdutoDTO tipoProdutoDTO) {
        TipoProduto tipoProduto = new TipoProduto();
        tipoProduto.setNome(tipoProdutoDTO.nome);
        tipoProduto.setCor(tipoProdutoDTO.cor);
        return tipoProduto;
    }

}
