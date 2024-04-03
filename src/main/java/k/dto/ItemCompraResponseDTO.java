package k.dto;

import jakarta.validation.constraints.NotBlank;
import k.model.ItemCompra;

public record ItemCompraResponseDTO(
        Long idItemCompra,
        @NotBlank Long produtoId,
        String nome,
        @NotBlank Integer quantidade,
        Double preco) {
    public ItemCompraResponseDTO(ItemCompra i) {
        this(i.getId(), i.getProduto().getId(), i.getProduto().getNome(), i.getQuantidade(), i.getPreco());
    }

}
