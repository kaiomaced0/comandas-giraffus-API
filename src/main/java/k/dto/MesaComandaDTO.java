package k.dto;

public record MesaComandaDTO(
        Long id,
        String nome,
        Double preco,
        Boolean finalizada) {
}
