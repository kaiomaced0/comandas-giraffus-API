package k.dto;

import java.math.BigDecimal;

public record TopProdutoDTO(
        Long produtoId,
        String nome,
        long quantidade,
        BigDecimal receita) {
}
