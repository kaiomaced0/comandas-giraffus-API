package k.dto;

public record ProdutoAdicionaRetiraDTO(
        Long id,
        Integer quantidade,
        String motivo) {

    public ProdutoAdicionaRetiraDTO(Long id, Integer quantidade) {
        this(id, quantidade, null);
    }
}
