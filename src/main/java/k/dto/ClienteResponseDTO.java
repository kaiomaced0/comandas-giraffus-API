package k.dto;

public record ClienteResponseDTO(
        Long id,
        String cpf,
        String nome,
        String email) {
}
