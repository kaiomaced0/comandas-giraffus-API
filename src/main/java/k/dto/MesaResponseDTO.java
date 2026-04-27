package k.dto;

public record MesaResponseDTO(
        Long id,
        String identificador,
        Integer capacidade,
        Boolean ativo) {
}
